import re
import json
import os
from datetime import datetime

def get_experiment_info(loaded_data, experiment_id):
    """
    Finds the experiment by ID in the already loaded JSON data.
    Returns (experiment_type, generations_data)
    """
    if not loaded_data:
        return None, []

    # Check Simple
    simple_data = loaded_data.get("simple", {}).get("experiments", [])
    for exp in simple_data:
        if exp["id"] == experiment_id:
            return exp.get("type", "simple"), exp.get("generations", [])

    # Check CoT
    cot_data = loaded_data.get("cot", {}).get("experiments", [])
    for exp in cot_data:
        if exp["id"] == experiment_id:
            return exp.get("type", "cot"), exp.get("generations", [])

    return None, []

def parse_metrics_markdown(metrics_path, loaded_experiments_data):
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

    # Get structure from pre-loaded JSON
    exp_type, generations_data = get_experiment_info(loaded_experiments_data, experiment_id)
    
    if not generations_data and exp_type is None:
        print(f"Warning: Experiment ID {experiment_id} not found. Skipping metrics parsing.")
        return {"metrics": []}

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
    metric_types = {
        "Syntax Errors": "syntax",
        "Multiplicities Errors": "multiplicities",
        "Invariants Errors": "invariants"
    }
    
    for label, key in metric_types.items():
        match = re.search(r'\|\s*' + label + r'\s*\|\s*(\d+)\s*\|\s*(\d+)', text)
        if match:
            data[key]["errors"] = int(match.group(1))
            data[key]["total"] = int(match.group(2))

    # 2. Identify the "Specific Metrics" Tables
    header_matches = re.finditer(r'^\|\s*([^|]+?)\s*\|\s*(?:Invalid|Errors)\s*\|\s*Total', text, re.MULTILINE)
    
    specific_metrics_map = {} # Maps normalized_key -> original_row_name
    redirect_metrics_map = {} # Maps data_key -> original_row_name for non-specific items
    
    for match in header_matches:
        header_name = match.group(1).strip()
        if "General" in header_name or "Failed" in header_name or "Invalid" in header_name:
            continue
            
        # Found a specific table header. Now parse its rows.
        start_pos = match.end() # This is end of match in reference to 'Total'
        
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
            row_match = re.match(r'\|\s*([^|]+?)\s*\|\s*(\d+)\s*\|\s*(\d+)', line)
            if row_match:
                metric_name_raw = row_match.group(1).strip()
                invalid_count = int(row_match.group(2))
                total_count = int(row_match.group(3))
                
                # Check for special redirects for "Not included on General"
                if "Multiplicities Errors (Not included on General)" in metric_name_raw:
                    if "multiplicities_category_invalid" not in data:
                        data["multiplicities_category_invalid"] = {"errors": 0, "total": 0, "str": []}
                    data["multiplicities_category_invalid"]["errors"] = invalid_count
                    data["multiplicities_category_invalid"]["total"] = total_count
                    redirect_metrics_map["multiplicities_category_invalid"] = metric_name_raw
                    continue
                
                if "Invariants Errors (Not included on General)" in metric_name_raw:
                    if "invariants_category_invalid" not in data:
                        data["invariants_category_invalid"] = {"errors": 0, "total": 0, "str": []}
                    data["invariants_category_invalid"]["errors"] = invalid_count
                    data["invariants_category_invalid"]["total"] = total_count
                    redirect_metrics_map["invariants_category_invalid"] = metric_name_raw
                    continue

                if "Not included on General" in metric_name_raw:
                    # Ignore other unknown "Not included" types or handle as specific?
                    # Current instructions say handle multiplicities/invariants. 
                    # If there's others, we can treat them as specific or skip.
                    # Let's treat them as specific if they don't match the above.
                    pass

                # Normalize key: "IBANs (Real)" -> "ibans_real"
                normalized_key = re.sub(r'[()]', '', metric_name_raw).strip().lower().replace(' ', '_')
                
                data["specific"][normalized_key] = {
                    "errors": invalid_count,
                    "total": total_count,
                    "str": []
                }
                specific_metrics_map[normalized_key] = metric_name_raw
        
        # We process all tables found.

    # 3. Parse Failed Strings for each specific metric found
    def extract_failed_strings(metric_raw_name):
        escaped_name = re.escape(metric_raw_name)
        patterns = [
            r'\|\s*Failed ' + escaped_name + r'\s*\|',
            r'\|\s*Invalid ' + escaped_name + r'\s*\|',
            # Handle [Overconstraints Detection] or similar prefixes
            r'\|\s*\[.*?\] ' + escaped_name + r'\s*\|', 
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

    # Populate string for specific
    for key, raw_name in specific_metrics_map.items():
        data["specific"][key]["str"] = extract_failed_strings(raw_name)
        if not data["specific"][key]["str"]:
             data["specific"][key]["str"] = [""]

    # Populate string for redirected metrics
    for key, raw_name in redirect_metrics_map.items():
        data[key]["str"] = extract_failed_strings(raw_name)
        if not data[key]["str"]:
             data[key]["str"] = [""]

    # Fallback to [""] if empty for General metrics
    for cat in ["syntax", "multiplicities", "invariants", "multiplicities_category_invalid", "invariants_category_invalid"]:
        if cat in data and not data[cat]["str"]: data[cat]["str"] = [""]

    return {k: data[k] for k in [
        "syntax", 
        "multiplicities", 
        "invariants", 
        "multiplicities_category_invalid", 
        "invariants_category_invalid", 
        "specific"
    ] if k in data}

if __name__ == "__main__":
    target_json_source = "logs.json"
    
    loaded_data = {}
    if os.path.exists(target_json_source):
        with open(target_json_source, 'r', encoding='utf-8') as f:
            loaded_data = json.load(f)

    files = [
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/addressbook/GPT_4O/21-03-2025--17-36-43/metrics.md",
        "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/bank/GPT_4O/21-03-2025--15-41-00/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/football/GPT_4O/02-04-2025--15-50-01/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/hotelmanagement/GPT_4O/22-03-2025--18-31-34/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/myexpenses/GPT_4O/22-03-2025--17-12-34/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/pickupnet/GPT_4O/21-03-2025--15-53-43/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/restaurant/GPT_4O/02-04-2025--16-10-47/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/statemachine/GPT_4O/22-03-2025--18-08-16/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/vehiclerental/GPT_4O/21-03-2025--16-07-51/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/videoclub/GPT_4O/21-03-2025--16-37-44/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/addressbook/GPT_4O/25-03-2025--10-38-22/metrics.md",
        "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/bank/GPT_4O/24-03-2025--21-13-02/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/football/GPT_4O/02-04-2025--19-03-28/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/hotelmanagement/GPT_4O/25-03-2025--11-38-06/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/myexpenses/GPT_4O/25-03-2025--13-12-54/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/pickupnet/GPT_4O/25-03-2025--13-32-07/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/restaurant/GPT_4O/02-04-2025--18-15-54/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/statemachine/GPT_4O/25-03-2025--13-49-01/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/vehiclerental/GPT_4O/25-03-2025--14-02-01/metrics.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/videoclub/GPT_4O/25-03-2025--14-18-14/metrics.md",
    ]
    
    output_metrics = {
        "simple": {"experiments": []},
        "cot": {"experiments": []}
    }
    
    for m_path in files:
        if os.path.exists(m_path):
            try:
                result = parse_metrics_markdown(m_path, loaded_data)
                # result["metrics"] matches the structure returned by parse_metrics_markdown
                # which returns [{"experiment_id": ..., "generations": ...}]
                for m_exp in result["metrics"]:
                    # We need to decide where to put it. 
                    # We can use get_experiment_info again or infer from ID
                    eid = m_exp["experiment_id"]
                    if eid.lower().startswith("cot"):
                        output_metrics["cot"]["experiments"].append(m_exp)
                    else:
                        output_metrics["simple"]["experiments"].append(m_exp)
                        
                print(f"Processed metrics for: {m_path}")
            except Exception as e:
                print(f"Error processing metrics {m_path}: {e}")
                import traceback
                traceback.print_exc()
        else:
            # print(f"Metrics file not found: {m_path}")
            pass
            
    output_path = "metrics.json"
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(output_metrics, f, indent=2)
