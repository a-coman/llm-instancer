import re
import json
import os
from datetime import datetime

def parse_logs_markdown(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # DETECT EXPERIMENT TYPE (Simple vs CoT)
    exp_type = "simple"
    if "/CoT/" in file_path or "/cot/" in file_path:
        exp_type = "cot"

    # EXTRACT METADATA
    path_parts = file_path.split(os.sep)
    try:
        timestamp_dir = path_parts[-2]
        model_name = path_parts[-3]
        domain_name = path_parts[-4]
        type_str = path_parts[-5] # "Simple" or "CoT"
        
        date_obj = datetime.strptime(timestamp_dir, "%d-%m-%Y--%H-%M-%S")
        date_str = date_obj.strftime("%Y-%m-%d")
        time_str = date_obj.strftime("%H:%M:%S")
    except:
        timestamp_dir = "unknown"
        model_name = "unknown"
        domain_name = "unknown"
        date_str = "unknown"
        time_str = "unknown"
        type_str = exp_type.capitalize()

    experiment_id = f"{type_str}-{domain_name}-{timestamp_dir}"
    
    # SYSTEM PROMPT EXTRACTION
    # Find the *first* system message in the file
    sys_match = re.search(r'SystemMessage \{ text = "(.*?)" \}', content, re.DOTALL)
    system_prompt = ""
    if sys_match:
        system_prompt = sys_match.group(1).replace('\\"', '"').replace('\n', ' ')

    # ROBUST PARSING OF BLOCKS
    # Split by headers: # Input | # Output | # Summary
    data_map = {}

    # Split into sections using headers
    # We allow some whitespace around the header
    header_pattern = r'^# (Input|Output|Summary for all generations)(.*?)$'
    parts = re.split(header_pattern, content, flags=re.MULTILINE)
    
    # parts[0] is preamble
    current_sys_prompt = ""
    last_seen_prompt = ""
    i = 1
    while i < len(parts):
        io_type = parts[i]
        header_text = parts[i+1].strip()
        body = parts[i+2]
        i += 3
        
        if io_type == "Summary for all generations":
            continue
            
        # Parse Type and ID
        if ":" in header_text:
            b_type, b_id = [s.strip() for s in header_text.split(":", 1)]
        else:
            b_type = header_text
            b_id = ""
            
        key = (b_type, b_id)
        if key not in data_map:
            data_map[key] = {
                "prompt": "",
                "attempts_data": []
            }
            
        if io_type == "Input":
            # Check for SystemMessage
            sys_match_inner = re.search(r'SystemMessage \{ text = "(.*?)" \}', body, re.DOTALL)
            if sys_match_inner:
                current_sys_prompt = sys_match_inner.group(1).replace('\\"', '"').replace('\n', ' ')

            # Extract prompt
            user_match = re.search(r'UserMessage \{.*?text = (["\'])(.*?)\1 \}\] \}', body, re.DOTALL)
            if user_match:
                prompt_text = user_match.group(2).replace('\\"', '"').replace('\\n', '\n')
                data_map[key]["current_prompt"] = prompt_text
                last_seen_prompt = prompt_text
            
            # Store system prompt for this key
            data_map[key]["system_prompt"] = current_sys_prompt

        elif io_type == "Output":
            # Inherit system prompt if not set (e.g. if Input is missing or interleaved)
            if "system_prompt" not in data_map[key]:
                data_map[key]["system_prompt"] = current_sys_prompt

            # Extract response and metrics
            response_text = ""
            output_code_match = re.search(r'```(.*?)```', body, re.DOTALL)
            if output_code_match:
                response_text = output_code_match.group(1).strip()
            
            # Metrics
            finish_reason = re.search(r'Finish Reason:\s*(\w+)', body)
            input_tokens = re.search(r'Input Tokens:\s*(\d+)', body)
            output_tokens = re.search(r'Output Tokens:\s*(\d+)', body)
            total_tokens = re.search(r'Total Tokens:\s*(\d+)', body)
            time_seconds = re.search(r'Generation Time:\s*([\d\.]+)', body)
            
            # Use specific prompt for this key, or fallback to last seen prompt if missing (orphaned output)
            eff_prompt = data_map[key].get("current_prompt", "")
            if not eff_prompt:
                eff_prompt = last_seen_prompt
            
            attempt = {
                "prompt": eff_prompt,
                "response": response_text,
                "input_tokens": int(input_tokens.group(1)) if input_tokens else 0,
                "output_tokens": int(output_tokens.group(1)) if output_tokens else 0,
                "total_tokens": int(total_tokens.group(1)) if total_tokens else 0,
                "finish_reason": finish_reason.group(1).lower() if finish_reason else "unknown",
                "time_seconds": float(time_seconds.group(1)) if time_seconds else 0.0,
                "instance_name": "output"
            }
            data_map[key]["attempts_data"].append(attempt)

    # PARSE GLOBAL SUMMARY
    summary_data = {}
    summary_match = re.search(r'# Summary for all generations(.*)', content, re.DOTALL)
    if summary_match:
        summary_text = summary_match.group(1)
        sum_input = re.search(r'Sum of input tokens\s*\|\s*(\d+)', summary_text)
        sum_output = re.search(r'Sum of output tokens\s*\|\s*(\d+)', summary_text)
        sum_total = re.search(r'Sum of total tokens\s*\|\s*(\d+)', summary_text)
        gen_time = re.search(r'Generations time\s*\|\s*([\d\.]+)', summary_text)
        
        if sum_input: summary_data["input_tokens"] = int(sum_input.group(1))
        if sum_output: summary_data["output_tokens"] = int(sum_output.group(1))
        if sum_total: summary_data["total_tokens"] = int(sum_total.group(1))
        if gen_time: summary_data["time_seconds"] = float(gen_time.group(1))

    # PREPARE EXPERIMENT OBJECT
    final_experiment = {
        "id": experiment_id,
        "type": exp_type,
        "date": date_str,
        "time": time_str,
        "model": {
            "name": model_name,
            "provider": "unknown",
            "version": "unknown"
        },
        "domain_name": domain_name,
        "system_prompt": system_prompt,
        "input_tokens": summary_data.get("input_tokens", 0),
        "output_tokens": summary_data.get("output_tokens", 0),
        "total_tokens": summary_data.get("total_tokens", 0),
        "time_seconds": summary_data.get("time_seconds", 0.0),
    }

    # Handle IModelAnalyzer specially
    if ("IModelAnalyzer", "") in data_map:
        ma = data_map[("IModelAnalyzer", "")]
        final_experiment["IModelAnalyzer"] = {
            "system_prompt": ma.get("system_prompt", system_prompt), # Use granular or global fallback
            "prompt": ma.get("current_prompt", ""),
            "response": ma["attempts_data"][0]["response"] if ma["attempts_data"] else "",
            "input_tokens": ma["attempts_data"][0]["input_tokens"] if ma["attempts_data"] else 0,
            "output_tokens": ma["attempts_data"][0]["output_tokens"] if ma["attempts_data"] else 0,
            "total_tokens": ma["attempts_data"][0]["total_tokens"] if ma["attempts_data"] else 0,
            "time_seconds": ma["attempts_data"][0]["time_seconds"] if ma["attempts_data"] else 0.0
        }

    # Group into Generations
    all_blocks = []
    for (b_type, b_id), val in data_map.items():
        if b_type == "IModelAnalyzer": continue
        
        match = re.search(r'^([a-zA-Z]*)(\d+)$', b_id)
        if match:
            cat_name = match.group(1).lower()
            gen_num = match.group(2)
        else:
            cat_name = ""
            gen_num = b_id if b_id.isdigit() else "0"
            
        block = val.copy()
        block["_type"] = b_type
        block["_gen_num"] = gen_num
        block["_cat_name"] = cat_name
        all_blocks.append(block)

    if exp_type == "cot" or any(b["_type"].startswith("IList") for b in all_blocks):
        # CoT Grouping
        gen_map = {}
        for b in all_blocks:
            gn = b["_gen_num"]
            cn = b["_cat_name"]
            ph = b["_type"]
            
            if gn not in gen_map: gen_map[gn] = {}
            if cn not in gen_map[gn]: gen_map[gn][cn] = {}
            
            if ph not in gen_map[gn][cn]:
                 gen_map[gn][cn][ph] = { 
                     "prompt": b["prompt"], 
                     "system_prompt": b.get("system_prompt", ""),
                     "attempts": [] 
                 }
            else:
                 if not gen_map[gn][cn][ph]["prompt"]:
                     gen_map[gn][cn][ph]["prompt"] = b["prompt"]
            
            for idx, ad in enumerate(b["attempts_data"]):
                 status = "success" if idx == len(b["attempts_data"]) - 1 else "failed"
                 gen_map[gn][cn][ph]["attempts"].append({
                     "id": str(idx + 1),
                     "status": status,
                     "prompt": ad["prompt"],
                     "response": ad["response"],
                     "instance_name": ad["instance_name"],
                     "input_tokens": ad["input_tokens"],
                     "output_tokens": ad["output_tokens"],
                     "total_tokens": ad["total_tokens"],
                     "finish_reason": ad["finish_reason"],
                     "time_seconds": ad["time_seconds"]
                 })

        generations_list = []
        # Sort generations by numeric order
        sorted_gn = sorted(gen_map.keys(), key=lambda x: int(x) if x.isdigit() else 999)
        for gn in sorted_gn:
            cats_list = []
            for cn in sorted(gen_map[gn].keys()):
                cat_obj = {"name": cn}
                for ph in ["IListCreator", "IListInstantiator"]:
                    if ph in gen_map[gn][cn]:
                        phase_data = gen_map[gn][cn][ph]
                        cat_obj[ph] = { 
                            "system_prompt": phase_data.get("system_prompt", ""),
                            "attempts": phase_data["attempts"] 
                        }
                
                cats_list.append(cat_obj)
            
            generations_list.append({
                "id": str(gn),
                "categories": cats_list
            })
        
        final_experiment["number_generations"] = len(generations_list)
        final_experiment["generations"] = generations_list
        
    else:
        # Simple Grouping
        gen_map = {}
        for b in all_blocks:
            gn = b["_gen_num"]
            if gn not in gen_map: gen_map[gn] = { "attempts": [] }
            
            for idx, ad in enumerate(b["attempts_data"]):
                 status = "success" if idx == len(b["attempts_data"]) - 1 else "failed"
                 gen_map[gn]["attempts"].append({
                     "id": str(idx + 1),
                     "status": status,
                     "prompt": ad["prompt"],
                     "response": ad["response"],
                     "instance_name": ad["instance_name"],
                     "input_tokens": ad["input_tokens"],
                     "output_tokens": ad["output_tokens"],
                     "total_tokens": ad["total_tokens"],
                     "finish_reason": ad["finish_reason"],
                     "time_seconds": ad["time_seconds"]
                 })

        generations_list = []
        sorted_gn = sorted(gen_map.keys(), key=lambda x: int(x) if x.isdigit() else 999)
        for gn in sorted_gn:
            generations_list.append({
                "id": str(gn),
                "attempts": gen_map[gn]["attempts"]
            })
            
        final_experiment["number_generations"] = len(generations_list)
        final_experiment["generations"] = generations_list

    return {"experiments": [final_experiment]}

if __name__ == "__main__":
    files = [
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/addressbook/GPT_4O/21-03-2025--17-36-43/logs.md",
        "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/bank/GPT_4O/21-03-2025--15-41-00/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/football/GPT_4O/02-04-2025--15-50-01/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/hotelmanagement/GPT_4O/22-03-2025--18-31-34/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/myexpenses/GPT_4O/22-03-2025--17-12-34/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/pickupnet/GPT_4O/21-03-2025--15-53-43/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/restaurant/GPT_4O/02-04-2025--16-10-47/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/statemachine/GPT_4O/22-03-2025--18-08-16/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/vehiclerental/GPT_4O/21-03-2025--16-07-51/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/videoclub/GPT_4O/21-03-2025--16-37-44/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/addressbook/GPT_4O/25-03-2025--10-38-22/logs.md",
        "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/bank/GPT_4O/24-03-2025--21-13-02/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/football/GPT_4O/02-04-2025--19-03-28/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/hotelmanagement/GPT_4O/25-03-2025--11-38-06/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/myexpenses/GPT_4O/25-03-2025--13-12-54/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/pickupnet/GPT_4O/25-03-2025--13-32-07/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/restaurant/GPT_4O/02-04-2025--18-15-54/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/statemachine/GPT_4O/25-03-2025--13-49-01/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/vehiclerental/GPT_4O/25-03-2025--14-02-01/logs.md",
        # "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/videoclub/GPT_4O/25-03-2025--14-18-14/logs.md",
    ]
    
    # Prepare Output Structure
    output_data = {
        "simple": {
            "input_tokens": 0,
            "output_tokens": 0,
            "total_tokens": 0,
            "time_seconds": 0.0,
            "number_experiments": 0,
            "experiments": []
        },
        "cot": {
            "input_tokens": 0,
            "output_tokens": 0,
            "total_tokens": 0,
            "time_seconds": 0.0,
            "number_experiments": 0,
            "experiments": []
        }
    }

    for fpath in files:
        if os.path.exists(fpath):
            try:
                result = parse_logs_markdown(fpath)
                # result["experiments"] is a list, usually containing 1 experiment
                for exp in result["experiments"]:
                    # Determine type (simple or cot) based on exp['type']
                    # logic in parse_logs_markdown sets type to "simple" or "cot" (lowercase)
                    etype = exp["type"]
                    if etype not in output_data:
                        etype = "simple" # Fallback
                    
                    output_data[etype]["experiments"].append(exp)
                    
                    # Accumulate stats
                    output_data[etype]["input_tokens"] += exp.get("input_tokens", 0)
                    output_data[etype]["output_tokens"] += exp.get("output_tokens", 0)
                    output_data[etype]["total_tokens"] += exp.get("total_tokens", 0)
                    output_data[etype]["time_seconds"] += exp.get("time_seconds", 0.0)
                    
                print(f"Processed: {fpath}")
            except Exception as e:
                print(f"Error processing {fpath}: {e}")
                import traceback
                traceback.print_exc()

    # Update Counts
    output_data["simple"]["number_experiments"] = len(output_data["simple"]["experiments"])
    output_data["cot"]["number_experiments"] = len(output_data["cot"]["experiments"])

    # Round time
    output_data["simple"]["time_seconds"] = round(output_data["simple"]["time_seconds"], 2)
    output_data["cot"]["time_seconds"] = round(output_data["cot"]["time_seconds"], 2)

    with open("logs.json", 'w') as f:
        json.dump(output_data, f, indent=2)
