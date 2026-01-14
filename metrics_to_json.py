import re
import json
import os
from datetime import datetime

def get_experiment_info(json_path, experiment_id):
    """
    Reads experiments_data.json and finds the experiment by ID.
    Returns (experiment_type, generations_data)
    """
    if not os.path.exists(json_path):
        return None, []

    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    for exp in data.get("experiments", []):
        if exp["id"] == experiment_id:
             return exp.get("type", "simple"), exp.get("generations", [])
             
    return None, []

def parse_metrics_markdown(metrics_path, json_path):
    with open(metrics_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Extract metadata for experiment_id
    path_parts = metrics_path.split(os.sep)
    try:
        timestamp_dir = path_parts[-2]
        # Logic to infer ID parts from path matches log_to_json
        type_name = path_parts[-5] # Raw type name
        domain_name = path_parts[-4]
        
        # Normalize Type Case (Simple vs CoT)
        # Log parser used specific capitalization.
        # "Simple", "CoT".
        parsed_type = type_name
        if type_name.lower() == "simple": parsed_type = "Simple"
        if type_name.lower() == "cot": parsed_type = "CoT"
        
        experiment_id = f"{parsed_type}-{domain_name}-{timestamp_dir}"
    except:
        experiment_id = "unknown"

    # Get structure from existing JSON
    exp_type, generations_data = get_experiment_info(json_path, experiment_id)
    
    if not generations_data and exp_type is None:
        print(f"Warning: Experiment ID {experiment_id} not found in {json_path}. Skipping lookup logic.")
        # Default defaults
        exp_type = "simple"
        generations_data = []

    result_generations = []
    global_metrics = None
    
    # Split by unified header (Generation or Summary)
    parts = re.split(r'^# (Generation \d+(?:\.\d+)?|Summary for all generations)', content, flags=re.MULTILINE)
    
    # Create a lookup
    gen_lookup = {str(g["id"]): g for g in generations_data}
    
    # parts[0] is preamble
    i = 1
    while i < len(parts):
        header = parts[i].strip()
        body = parts[i+1]
        
        if header.startswith("Summary"):
             # Parse Overall Summary
             global_metrics = parse_single_metric_block(body)
             
        elif header.startswith("Generation"):
            # Extract ID from header "Generation 1" -> "1"
            # It might be "Generation 1" or "Generation 1.0"
            gen_id_raw = header.split(" ")[1]
            gen_id = gen_id_raw
            
            # Identify appropriate generation object
            gen_data_json = gen_lookup.get(gen_id)
            
            if gen_data_json:
                if exp_type == "cot":
                    # CoT Structure: Generation -> Categories ... -> Generation Summary
                    
                    # 1. Extract Generation Summary (usually at the end)
                    # We split by "## Generation <ID> summary"
                    # Note: ID might be simple number or match gen_id
                    
                    summary_split = re.split(r'^## Generation .*? summary', body, flags=re.MULTILINE)
                    
                    if len(summary_split) > 1:
                        categories_text = summary_split[0]
                        summary_text = summary_split[1]
                        gen_metrics = parse_single_metric_block(summary_text)
                    else:
                        # Fallback: maybe summary is missing or format differs
                        categories_text = body
                        gen_metrics = parse_single_metric_block("") # Empty/Zeros
                    
                    # 2. Split Categories
                    category_split = re.split(r'(^## Category [a-zA-Z0-9_]+)', categories_text, flags=re.MULTILINE)
                    
                    categories_list = []
                    current_parsed_cats = []

                    # category_split[0] is text before first category (should be empty/whitespace)
                    # We iterate from index 1: [Header1, Body1, Header2, Body2...]
                    
                    j = 1
                    while j < len(category_split) - 1:
                        cat_header = category_split[j] # e.g. ## Category baseline
                        cat_body = category_split[j+1]
                        
                        cat_name_match = re.search(r'## Category ([a-zA-Z0-9_]+)', cat_header)
                        cat_name = cat_name_match.group(1).strip() if cat_name_match else "unknown"
                        
                        # CLEAN NAME: remove trailing digits (baseline1 -> baseline)
                        clean_name = re.sub(r'\d+$', '', cat_name)
                        
                        # Find attempt ID logic
                        success_attempt_id = "1"
                        json_cat = next((c for c in gen_data_json.get("categories", []) if c["name"] == clean_name), None)
                        if json_cat:
                             attempts = json_cat.get("IListInstantiator", {}).get("attempts", [])
                             if attempts:
                                 success_attempt_id = str(attempts[-1]["id"])
                        
                        # Parse Metrics
                        cat_metrics = parse_single_metric_block(cat_body)
                        
                        # Special handling for 'invalid' category Overconstraints
                        # Special handling for 'invalid' category Overconstraints
                        if clean_name == "invalid":
                             # Regex fix: use [^|]* to avoid greedy matching across columns
                             # Format: | Multiplicities Errors ... | Errors | Total |
                             
                             match_mul_over = re.search(r'\|\s*Multiplicities Errors[^|]*\|\s*(\d+)\s*\|\s*(\d+)', cat_body)
                             if match_mul_over:
                                 cat_metrics["multiplicities"]["errors"] = int(match_mul_over.group(1))
                                 cat_metrics["multiplicities"]["total"] = int(match_mul_over.group(2))
                                 
                             match_inv_over = re.search(r'\|\s*Invariants Errors[^|]*\|\s*(\d+)\s*\|\s*(\d+)', cat_body)
                             if match_inv_over:
                                 cat_metrics["invariants"]["errors"] = int(match_inv_over.group(1))
                                 cat_metrics["invariants"]["total"] = int(match_inv_over.group(2))
                             
                             # Remove these from 'specific' if they were auto-detected there
                             keys_to_remove = [k for k in cat_metrics["specific"] if "not_included_on_general" in k]
                             for k in keys_to_remove:
                                 del cat_metrics["specific"][k]

                        current_parsed_cats.append({
                            "name": clean_name,
                            "attempt_id": success_attempt_id,
                            "metrics": cat_metrics
                        })
                        j += 2
                    
                    # Post-processing for 'invalid' category syntax derivation
                    # Syntax Invalid = GenSyntax - Sum(OtherCatsSyntax)
                    invalid_cat_obj = next((c for c in current_parsed_cats if c["name"] == "invalid"), None)
                    
                    if invalid_cat_obj:
                        sum_errors = 0
                        sum_total = 0
                        for c in current_parsed_cats:
                            if c["name"] != "invalid":
                                sum_errors += c["metrics"]["syntax"]["errors"]
                                sum_total += c["metrics"]["syntax"]["total"]
                        
                        # Calculate derived syntax
                        derived_errors = gen_metrics["syntax"]["errors"] - sum_errors
                        derived_total = gen_metrics["syntax"]["total"] - sum_total
                        
                        # Assign (ensure non-negative just in case)
                        invalid_cat_obj["metrics"]["syntax"]["errors"] = max(0, derived_errors)
                        invalid_cat_obj["metrics"]["syntax"]["total"] = max(0, derived_total)

                    result_generations.append({
                        "generation_id": gen_id,
                        "categories": current_parsed_cats,
                        "metrics": gen_metrics
                    })
                    
                else:
                    # Simple Structure: Flat Generation
                    # Find successful logic
                    success_attempt_id = "1"
                    attempts = gen_data_json.get("attempts", [])
                    
                    # Search for success (last one is marked success by log_to_json)
                    for att in attempts:
                        if att.get("status") == "success":
                            success_attempt_id = str(att["id"])
                            break
                    else:
                        # Fallback to last
                        if attempts:
                            success_attempt_id = str(attempts[-1]["id"])
                    
                    gen_metrics = parse_single_metric_block(body)
                    
                    result_generations.append({
                        "generation_id": gen_id,
                        "attempt_id": success_attempt_id,
                        "metrics": gen_metrics
                    })

        i += 2

    # Construct final object
    final_output = {
        "experiment_id": experiment_id,
        "generations": result_generations,
    }
    
    if global_metrics:
        final_output["metrics"] = global_metrics

    return {"metrics": [final_output]}

def parse_single_metric_block(text):
    data = {
        "syntax": {"errors": 0, "total": 0, "str": []},
        "multiplicities": {"errors": 0, "total": 0, "str": []},
        "invariants": {"errors": 0, "total": 0, "str": []},
        "specific": {} 
    }

    # 1. Parse General Table (Syntax, Multiplicities, Invariants)
    match_syn = re.search(r'\|\s*Syntax Errors\s*\|\s*(\d+)\s*\|\s*(\d+)', text)
    if match_syn:
        data["syntax"]["errors"] = int(match_syn.group(1))
        data["syntax"]["total"] = int(match_syn.group(2))

    match_mul = re.search(r'\|\s*Multiplicities Errors\s*\|\s*(\d+)\s*\|\s*(\d+)', text)
    if match_mul:
        data["multiplicities"]["errors"] = int(match_mul.group(1))
        data["multiplicities"]["total"] = int(match_mul.group(2))

    match_inv = re.search(r'\|\s*Invariants Errors\s*\|\s*(\d+)\s*\|\s*(\d+)', text)
    if match_inv:
        data["invariants"]["errors"] = int(match_inv.group(1))
        data["invariants"]["total"] = int(match_inv.group(2))

    # 2. Identify the "Specific Metrics" Table
    # The General table usually ends with Invariants Errors or similar. 
    # We look for the NEXT table start. A table starts with | <Name> | Invalid | Total | ... 
    # We skip the General table itself.
    
    # Find all table headers: | Col1 | Col2 | Col3 | ...
    # We assume specific metrics table has header format: | <DomainName> | Invalid | Total | ...
    # AND it is NOT "General" and NOT "Failed..."
    
    # Find all table headers: | DomainName | ...
    # Specific metrics table usually follows General table.
    # It has a header row. We look for a header row that is NOT the General one.
    # The General table has headers: | General | Errors | Total | Failure (%) |
    # The Specific table has headers: | <Domain> | Invalid/Errors | Total | Failure (%) |
    
    # We look for lines starting with | that have "Total" and ("Invalid" or "Errors") in them
    # and the first column is NOT "General" and NOT starting with "Failed"
    
    header_matches = re.finditer(r'^\|\s*([^|]+?)\s*\|\s*(?:Invalid|Errors)\s*\|\s*Total', text, re.MULTILINE)
    
    specific_metrics_map = {} # Maps normalized_key -> original_row_name
    
    for match in header_matches:
        header_name = match.group(1).strip()
        if "General" in header_name or "Failed" in header_name or "Invalid" in header_name:
            continue
            
        # Found the specific table header. Now parse its rows.
        start_pos = match.end() # This is end of match in reference to 'Total', we need end of line really
        
        # Advance start_pos to end of this line
        params_end = text.find('\n', start_pos)
        if params_end != -1:
             start_pos = params_end + 1
        else:
             break # No rows?
        
        rest_of_text = text[start_pos:]
        lines = rest_of_text.split('\n')
        
        for line in lines:
            line = line.strip()
            if not line: continue 
            if "---" in line: continue # Separator line
            if not line.startswith("|"): break # End of table
            
            # Row format: | Metric Name | Invalid Count | Total Count | ...
            # Regex: | Name | Number | Number | ...
            row_match = re.match(r'\|\s*([^|]+?)\s*\|\s*(\d+)\s*\|\s*(\d+)', line)
            if row_match:
                metric_name_raw = row_match.group(1).strip()
                # If metric name is just "Total" or something aggregation-like, skip?? Usually rows are specific metrics.
                if "Not included on General" in metric_name_raw:
                    continue                
                invalid_count = int(row_match.group(2))
                total_count = int(row_match.group(3))
                
                # Normalize key: "IBANs (Real)" -> "ibans_real"
                normalized_key = re.sub(r'[()]', '', metric_name_raw).strip().lower().replace(' ', '_')
                
                data["specific"][normalized_key] = {
                    "errors": invalid_count,
                    "total": total_count,
                    "str": []
                }
                specific_metrics_map[normalized_key] = metric_name_raw
        
        # We only expect one such specific table per block, so break after processing it
        break
        
    # 3. Parse Failed Strings for each specific metric found
    # Look for tables like: | Failed <Original Name> | or | Invalid <Original Name> |
    
    def extract_failed_strings(metric_raw_name):
        # Escape for regex
        escaped_name = re.escape(metric_raw_name)
        
        # We need to handle cases where the failure table header might slightly differ, 
        # e.g. "Addresses" vs "Invalid Addresses" vs "Failed Addresses"
        # The prompt says patterns are "| Failed IBANs (Real) |" or "| Invalid Addresses |"
        
        patterns = [
            r'\|\s*Failed ' + escaped_name + r'\s*\|',
            r'\|\s*Invalid ' + escaped_name + r'\s*\|',
            # Fallback for just the name if it's unique enough? No, "Addresses" is too common.
            # Maybe check for "Failed" + partial match?
        ]
        
        for header_regex in patterns:
            pattern = header_regex + r'(.*?)(?=\n\|\s*(?:Failed|Invalid|General|[A-Z])|\n#|$)'
            section_match = re.search(pattern, text, re.DOTALL)
            if section_match:
                section_text = section_match.group(1)
                found = re.findall(r'```\s*(.*?)\s*```', section_text, re.DOTALL)
                found = [s.strip() for s in found if s.strip()]
                return found
        
        return [""]

    for key, raw_name in specific_metrics_map.items():
        data["specific"][key]["str"] = extract_failed_strings(raw_name)
        if not data["specific"][key]["str"]:
             data["specific"][key]["str"] = [""]

    # Fallback to [""] if empty for General metrics
    for cat in ["syntax", "multiplicities", "invariants"]:
        if not data[cat]["str"]: data[cat]["str"] = [""]

    return data

if __name__ == "__main__":
    target_json_source = "experiments_data.json"
    files = [
        "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/bank/GPT_4O/21-03-2025--15-41-00/metrics.md",
        "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/bank/GPT_4O/24-03-2025--21-13-02/metrics.md"
    ]
    
    all_metrics = []
    
    for m_path in files:
        if os.path.exists(m_path):
            try:
                result = parse_metrics_markdown(m_path, target_json_source)
                all_metrics.extend(result["metrics"])
                print(f"Processed metrics for: {m_path}")
            except Exception as e:
                print(f"Error processing metrics {m_path}: {e}")
        else:
            print(f"Metrics file not found: {m_path}")
            
    output_path = "metrics_data.json"
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump({"metrics": all_metrics}, f, indent=2)
        
    print(f"Successfully converted all metrics to {output_path}")
