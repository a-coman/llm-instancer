import re
import json
import os
from datetime import datetime

def get_experiment_info(json_path):
    """
    Reads experiments_data.json to get the experiment type and structure.
    Returns (experiment_type, generations_data)
    """
    if not os.path.exists(json_path):
        return None, []

    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    if not data.get("experiments"):
        return None, []
        
    experiment = data["experiments"][0]
    exp_type = experiment.get("type", "simple")
    generations = experiment.get("generations", [])
    
    return exp_type, generations

def parse_metrics_markdown(metrics_path, json_path):
    # Get structure from existing JSON
    exp_type, generations_data = get_experiment_info(json_path)
    
    with open(metrics_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Extract metadata for experiment_id
    path_parts = metrics_path.split(os.sep)
    try:
        timestamp_dir = path_parts[-2]
        domain_name = path_parts[-4]
        type_name = path_parts[-5] # Raw type name from folder
        experiment_id = f"{type_name}-{domain_name}-{timestamp_dir}"
    except:
        experiment_id = "unknown"

    result_generations = []
    
    # Split by Generation header
    # Matches "# Generation 1", "# Generation 5.1", etc.
    gen_parts = re.split(r'^# Generation (\d+(?:\.\d+)?)', content, flags=re.MULTILINE)
    
    # Handle Global Summary separately (at end usually)
    global_metrics = None
    summary_match = re.search(r'^# Summary for all generations\n(.*?)$', content, re.DOTALL|re.MULTILINE)
    if summary_match:
        global_metrics = parse_single_metric_block(summary_match.group(1))

    # Map iterations to JSON structure
    # gen_parts: [preamble, id, body, id, body, ...]
    
    # Create a lookup for generations data to easily find attempt_ids
    # For CoT, generations_data is list of {id, categories: [...]}
    # For Simple, generations_data is list of {id, attempts: [...]}
    gen_lookup = {g["id"]: g for g in generations_data}
    
    i = 1
    while i < len(gen_parts):
        gen_id = gen_parts[i]
        gen_body = gen_parts[i+1]
        
        # Check if we have data for this generation in our structure
        if gen_id in gen_lookup:
            gen_data_json = gen_lookup[gen_id]
            
            if exp_type == "cot":
                # CoT Structure: Generation -> Categories
                # We need to parse sub-headers in gen_body: ## Category <name>
                
                categories_list = []
                
                # Split body by Category header
                cat_parts = re.split(r'^## Category ([a-zA-Z0-9_]+)', gen_body, flags=re.MULTILINE)
                
                # cat_parts: [preamble, name, body, name, body...]
                j = 1
                while j < len(cat_parts):
                    cat_name = cat_parts[j]
                    cat_body = cat_parts[j+1]
                    
                    # Find attempt ID for this category in JSON
                    # In CoT, categories list contains objects with "name" and "IListInstantiator"
                    success_attempt_id = "1"
                    
                    # Find matching category in JSON
                    json_cat = next((c for c in gen_data_json.get("categories", []) if c["name"] == cat_name), None)
                    
                    if json_cat:
                        attempts = json_cat.get("IListInstantiator", {}).get("attempts", [])
                        # Use last attempt as successful one per logic
                        if attempts:
                            success_attempt_id = str(attempts[-1]["id"])
                    
                    cat_metrics = parse_single_metric_block(cat_body)
                    
                    categories_list.append({
                        "name": cat_name,
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
                # Find successful attempt ID
                success_attempt_id = "1"
                attempts = gen_data_json.get("attempts", [])
                
                # Logic: find marked success or last one
                found = False
                for att in attempts:
                    if att.get("status") == "success":
                        success_attempt_id = str(att["id"])
                        found = True
                        break
                if not found and attempts:
                    success_attempt_id = str(attempts[-1]["id"])
                
                gen_metrics = parse_single_metric_block(gen_body)
                
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
        # Lookahead for next header (start of line pipe OR start of line #)
        pattern = header_regex + r'(.*?)(?=\n\||\n#|$)'
        
        section_match = re.search(pattern, text, re.DOTALL)
        if not section_match:
            return [""] 
        
        section_text = section_match.group(1)
        
        # Extract content between code fences
        found = re.findall(r'```\s*(.*?)\s*```', section_text, re.DOTALL)
        
        cleaned_items = []
        for block in found:
             lines = block.split('\n')
             for line in lines:
                 s = line.strip()
                 if s: cleaned_items.append(s)
                 
        return cleaned_items if cleaned_items else [""]

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
    # target_metrics = "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/bank/GPT_4O/24-03-2025--21-13-02/metrics.md"
    target_metrics = "src/main/resources/instances/Simple/bank/GPT_4O/21-03-2025--15-41-00/metrics.md"
    
    if os.path.exists(target_metrics) and os.path.exists(target_json_source):
        result = parse_metrics_markdown(target_metrics, target_json_source)
        
        output_path = "metrics_data.json"
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(result, f, indent=2)
            
        print(f"Successfully converted metrics to {output_path}")
    else:
        print(f"Files not found: {target_metrics} or {target_json_source}")
