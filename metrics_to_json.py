import re
import json
import os
from datetime import datetime

def get_generation_ids_from_json(json_path):
    """
    Reads experiments_data.json to get the list of generation IDs and their successful attempt IDs.
    Returns a list of dicts: [{"id": "1", "attempt_id": 1}, ...]
    """
    if not os.path.exists(json_path):
        return []

    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    if not data.get("experiments"):
        return []
        
    experiment = data["experiments"][0]
    gen_info = []
    
    for gen in experiment.get("generations", []):
        group_id = gen["id"]
        # Find the successful attempt ID (or the last one if none marked success, though log_to_json guarantees one)
        successful_attempt_id = "1"
        for attempt in gen.get("attempts", []):
            if attempt.get("status") == "success":
                successful_attempt_id = str(attempt["id"])
                break
        else:
             # Fallback: take the last one if loop finishes without break
             if gen.get("attempts"):
                 successful_attempt_id = str(gen["attempts"][-1]["id"])

        gen_info.append({"id": group_id, "attempt_id": successful_attempt_id})
            
    return gen_info

def parse_metrics_markdown(metrics_path, json_path):
    with open(metrics_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Get IDs from the already processed JSON source of truth
    generation_info_list = get_generation_ids_from_json(json_path)
    
    # Extract metadata for experiment_id
    path_parts = metrics_path.split(os.sep)
    try:
        timestamp_dir = path_parts[-2]
        domain_name = path_parts[-4]
        type_name = path_parts[-5]
        experiment_id = f"{type_name}-{domain_name}-{timestamp_dir}"
    except:
        experiment_id = "unknown"

    experiment_metrics = {
        "experiment_id": experiment_id,
        "generations": [],
        "metrics": None
    }

    # Split into blocks
    parts = re.split(r'^# (Generation \d+|Summary for all generations)', content, flags=re.MULTILINE)
    
    gen_block_counter = 0
    
    for i in range(1, len(parts), 2):
        header = parts[i].strip()
        body = parts[i+1]
        
        if header.startswith("Generation"):
             # Get the corresponding info from our mapping
             if gen_block_counter < len(generation_info_list):
                 info = generation_info_list[gen_block_counter]
                 mapped_id = info["id"]
                 attempt_id = info["attempt_id"]
             else:
                 mapped_id = f"unknown-{gen_block_counter}"
                 attempt_id = "1"
             
             gen_metrics_obj = parse_single_metric_block(body)
             
             wrapper = {
                 "generation_id": mapped_id,
                 "attempt_id": attempt_id,
                 "metrics": gen_metrics_obj
             }
             experiment_metrics["generations"].append(wrapper)
             gen_block_counter += 1
             
        elif header.startswith("Summary"):
             # Parse Overall Summary -> "metrics" field at root
             experiment_metrics["metrics"] = parse_single_metric_block(body)

    return {"metrics": [experiment_metrics]}

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
        # Capture strictly until the next section start or end of block
        # Lookahead for: 
        # 1. Newline + Pipe + "Failed" (start of next failed section)
        # 2. Newline + "#" (start of next markdown header, though usually body is passed in)
        # 3. End of string ($)
        pattern = header_regex + r'(.*?)(?=\n\|\s*Failed|\n#|$)'
        
        section_match = re.search(pattern, text, re.DOTALL)
        if not section_match:
            return [""] 
        
        section_text = section_match.group(1)
        
        # Extract content between code fences
        found = re.findall(r'```\s*(.*?)\s*```', section_text, re.DOTALL)
        
        # Clean up whitespace and filter empty strings
        found = [s.strip() for s in found if s.strip()]
        
        return found if found else [""]

    data["specific"]["iban"]["str"] = extract_failed_strings(r'\|\s*Failed IBANs \(Real\)\s*\|')
    data["specific"]["bic"]["str"] = extract_failed_strings(r'\|\s*Failed BICs \(Realistic\)\s*\|')
    
    # Fallback to [""] if empty list meant "no errors" in user structure?
    for cat in ["syntax", "multiplicities", "invariants"]:
        if not data[cat]["str"]: data[cat]["str"] = [""]
    
    if not data["specific"]["iban"]["str"]: data["specific"]["iban"]["str"] = [""]
    if not data["specific"]["bic"]["str"]: data["specific"]["bic"]["str"] = [""]
    if not data["specific"]["countries"]["str"]: data["specific"]["countries"]["str"] = [""]

    return data

if __name__ == "__main__":
    target_metrics = "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/bank/GPT_4O/21-03-2025--15-41-00/metrics.md"
    target_json_source = "experiments_data.json" # Use the output from the previous script
    
    if os.path.exists(target_metrics):
        result = parse_metrics_markdown(target_metrics, target_json_source)
        
        output_path = "metrics_data.json"
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(result, f, indent=2)
            
        print(f"Successfully converted metrics to {output_path}")
    else:
        print(f"File not found: {target_metrics}")
