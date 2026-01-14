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
                    # CoT Structure: Generation -> Categories
                    # Split body by Category header: ## Category <name>
                    
                    categories_list = []
                    cat_parts = re.split(r'^## Category ([a-zA-Z0-9_]+)', body, flags=re.MULTILINE)
                    
                    j = 1
                    while j < len(cat_parts):
                        cat_name = cat_parts[j]
                        cat_body = cat_parts[j+1]
                        
                        # CLEAN NAME: remove trailing digits if present (e.g. baseline1 -> baseline)
                        clean_name = re.sub(r'\d+$', '', cat_name)
                        
                        # Find attempt ID logic
                        success_attempt_id = "1"
                        
                        # Find matching category in JSON (by name)
                        json_cat = next((c for c in gen_data_json.get("categories", []) if c["name"] == clean_name), None)
                        
                        if json_cat:
                             # Logic: Last attempt in IListInstantiator is the one.
                             attempts = json_cat.get("IListInstantiator", {}).get("attempts", [])
                             if attempts:
                                 success_attempt_id = str(attempts[-1]["id"])
                        
                        cat_metrics = parse_single_metric_block(cat_body)
                        
                        categories_list.append({
                            "name": clean_name,
                            "attempt_id": success_attempt_id,
                            "metrics": cat_metrics
                        })
                        j += 2
                    
                    result_generations.append({
                        "generation_id": gen_id,
                        "categories": categories_list
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
        "specific": {
            "iban": {"errors": 0, "total": 0, "str": []},
            "bic": {"errors": 0, "total": 0, "str": []},
            "countries": {"errors": 0, "total": 0, "str": []}
        }
    }

    # Regex patterns for tables
    
    # 1. Syntax
    match = re.search(r'\|\s*Syntax Errors\s*\|\s*(\d+)\s*\|\s*(\d+)', text)
    if match:
        data["syntax"]["errors"] = int(match.group(1))
        data["syntax"]["total"] = int(match.group(2))

    # 2. Multiplicities
    match = re.search(r'\|\s*Multiplicities Errors\s*\|\s*(\d+)\s*\|\s*(\d+)', text)
    if match:
        data["multiplicities"]["errors"] = int(match.group(1))
        data["multiplicities"]["total"] = int(match.group(2))

    # 3. Invariants
    match = re.search(r'\|\s*Invariants Errors\s*\|\s*(\d+)\s*\|\s*(\d+)', text)
    if match:
        data["invariants"]["errors"] = int(match.group(1))
        data["invariants"]["total"] = int(match.group(2))

    # 4. Specifics
    match = re.search(r'\|\s*IBANs \(Real\)\s*\|\s*(\d+)\s*\|\s*(\d+)', text)
    if match:
        data["specific"]["iban"]["errors"] = int(match.group(1))
        data["specific"]["iban"]["total"] = int(match.group(2))
    
    match = re.search(r'\|\s*BICs \(Realistic\)\s*\|\s*(\d+)\s*\|\s*(\d+)', text)
    if match:
        data["specific"]["bic"]["errors"] = int(match.group(1))
        data["specific"]["bic"]["total"] = int(match.group(2))
        
    match = re.search(r'\|\s*Countries \(Realistic\)\s*\|\s*(\d+)\s*\|\s*(\d+)', text)
    if match:
        data["specific"]["countries"]["errors"] = int(match.group(1))
        data["specific"]["countries"]["total"] = int(match.group(2))

    # Parse Failed Strings
    def extract_failed_strings(header_regex):
        # Capture strictly until the next section start (failed table) or end of block (header or EOF)
        pattern = header_regex + r'(.*?)(?=\n\|\s*Failed|\n#|$)'
        
        section_match = re.search(pattern, text, re.DOTALL)
        if not section_match:
            return [""] 
        
        section_text = section_match.group(1)
        
        # Extract content between code fences
        found = re.findall(r'```\s*(.*?)\s*```', section_text, re.DOTALL)
        
        # Clean up whitespace and filter empty strings (USER LOGIC)
        found = [s.strip() for s in found if s.strip()]
        
        return found if found else [""]

    data["specific"]["iban"]["str"] = extract_failed_strings(r'\|\s*Failed IBANs \(Real\)\s*\|')
    data["specific"]["bic"]["str"] = extract_failed_strings(r'\|\s*Failed BICs \(Realistic\)\s*\|')
    data["specific"]["countries"]["str"] = extract_failed_strings(r'\|\s*Failed Countries \(Realistic\)\s*\|')
    
    # Fallback to [""] if empty
    for cat in ["syntax", "multiplicities", "invariants"]:
        if not data[cat]["str"]: data[cat]["str"] = [""]
    
    if not data["specific"]["iban"]["str"]: data["specific"]["iban"]["str"] = [""]
    if not data["specific"]["bic"]["str"]: data["specific"]["bic"]["str"] = [""]
    if not data["specific"]["countries"]["str"]: data["specific"]["countries"]["str"] = [""]

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
