import re
import json
import os
from datetime import datetime

def extract_block_content(text):
    """
    Extracts content inside markdown code blocks ``` ... ```
    If multiple blocks, joins them.
    If no blocks, returns stripped text.
    """
    code_blocks = re.findall(r'```(?:[a-zA-Z]*)\n(.*?)```', text, re.DOTALL)
    if code_blocks:
        return "\n".join([b.strip() for b in code_blocks])
    return text.strip()

def parse_metrics_table(text):
    """
    Parses the |Response| table for tokens and time.
    """
    metrics = {
        "input_tokens": 0,
        "output_tokens": 0,
        "total_tokens": 0,
        "finish_reason": "unknown",
        "time_seconds": 0.0
    }
    
    # Simple regex search for keys
    in_tok = re.search(r'Input Tokens:\s*(\d+)', text)
    out_tok = re.search(r'Output Tokens:\s*(\d+)', text)
    tot_tok = re.search(r'Total Tokens:\s*(\d+)', text)
    fin_rsn = re.search(r'Finish Reason:\s*(\w+)', text)
    time_s = re.search(r'Generation Time:\s*([\d\.]+)', text)
    
    if in_tok: metrics["input_tokens"] = int(in_tok.group(1))
    if out_tok: metrics["output_tokens"] = int(out_tok.group(1))
    if tot_tok: metrics["total_tokens"] = int(tot_tok.group(1))
    if fin_rsn: metrics["finish_reason"] = fin_rsn.group(1).lower()
    if time_s: metrics["time_seconds"] = float(time_s.group(1))
    
    return metrics

def parse_cot_log(content, experiment):
    """
    Parses CoT style logs with IModelAnalyzer, IListCreator, IListInstantiator.
    Groups generations by ID ("1", "2") and then by Category ("baseline", "edge").
    """
    # 1. Parse IModelAnalyzer (Global)
    analyzer_data = {
        "system_prompt": "",
        "prompt": "",
        "response": "",
        "input_tokens": 0,
        "output_tokens": 0,
        "total_tokens": 0,
        "finish_reason": "unknown",
        "time_seconds": 0
    }
    
    # Extract Input IModelAnalyzer
    input_ma = re.search(r'# Input IModelAnalyzer\n(.*?)(?=\n# Output)', content, re.DOTALL)
    if input_ma:
        block = input_ma.group(1)
        # Extract SystemMessage
        sys_msg = re.search(r'SystemMessage \{ text = "(.*?)" \}', block, re.DOTALL)
        if sys_msg:
             analyzer_data["system_prompt"] = sys_msg.group(1).replace(r'\n', '\n')
        
        # Extract UserMessage (Prompt)
        user_msg = re.search(r'UserMessage .*? text = "(.*?)"', block, re.DOTALL)
        if user_msg:
             analyzer_data["prompt"] = user_msg.group(1).replace(r'\n', '\n')

    # Extract Output IModelAnalyzer
    output_ma = re.search(r'# Output IModelAnalyzer\n(.*?)(?=\n# Input|\Z)', content, re.DOTALL)
    if output_ma:
        block = output_ma.group(1)
        analyzer_data["response"] = extract_block_content(block)
        metrics = parse_metrics_table(block)
        analyzer_data.update(metrics)

    experiment["IModelAnalyzer"] = analyzer_data

    # 2. Parse Generations (Creator + Instantiator)
    # Map: gen_id -> { "categories_map": { cat_name: { ...category_obj... } } }
    gen_map = {}

    parts = re.split(r'^# (Input|Output) (IListCreator|IListInstantiator) : ([a-zA-Z]+)(\d+)', content, flags=re.MULTILINE)
    
    # Loop 5 items at a time: Match(Input/Output), Component, Category, ID, Body
    idx = 1
    while idx < len(parts):
        io_type = parts[idx]        # Input or Output
        component = parts[idx+1]    # IListCreator or IListInstantiator
        category = parts[idx+2]     # e.g. edge
        gen_num = parts[idx+3]      # e.g. 1
        body = parts[idx+4]         # Content
        
        # Normalize Gen ID to string
        gen_id = str(gen_num)
        
        # Ensure Generation Group exists
        if gen_id not in gen_map:
            gen_map[gen_id] = {} # Keyed by category name for parsing convenience
            
        # Ensure Category Object exists within Generation
        if category not in gen_map[gen_id]:
            gen_map[gen_id][category] = {
                "name": category,
                "IListCreator": {
                    "system_prompt": "", "prompt": "", "response": "", 
                    "input_tokens": 0, "output_tokens": 0, "total_tokens": 0, 
                    "finish_reason": "unknown", "time_seconds": 0
                },
                "IListInstantiator": {
                     "system_prompt": "",
                     "attempts": []
                }
            }

        target_cat = gen_map[gen_id][category]
        target_obj = target_cat[component]
        
        if component == "IListCreator":
            if io_type == "Input":
                sys_msg = re.search(r'SystemMessage \{ text = "(.*?)" \}', body, re.DOTALL)
                if sys_msg: target_obj["system_prompt"] = sys_msg.group(1).replace(r'\n', '\n')
                
                user_msg = re.search(r'UserMessage .*? text = "(.*?)"', body, re.DOTALL)
                if user_msg: target_obj["prompt"] = user_msg.group(1).replace(r'\n', '\n')
            
            elif io_type == "Output":
                target_obj["response"] = extract_block_content(body)
                metrics = parse_metrics_table(body)
                target_obj.update(metrics)

        elif component == "IListInstantiator":
            attempts = target_obj["attempts"]
            
            if io_type == "Input":
                # Capture System Prompt (stored on parent Instantiator object)
                sys_msg = re.search(r'SystemMessage \{ text = "(.*?)" \}', body, re.DOTALL)
                if sys_msg: 
                    target_obj["system_prompt"] = sys_msg.group(1).replace(r'\n', '\n')
                
                new_attempt = {
                    "id": str(len(attempts) + 1),
                    "status": "failed",
                    "prompt": "",
                    "response": "",
                    "instance_name": category,
                    "input_tokens": 0, "output_tokens": 0, "total_tokens": 0,
                    "finish_reason": "unknown", "time_seconds": 0
                }
                
                user_msg = re.search(r'UserMessage .*? text = "(.*?)"', body, re.DOTALL)
                if user_msg: new_attempt["prompt"] = user_msg.group(1).replace(r'\n', '\n')
                
                attempts.append(new_attempt)
                
            elif io_type == "Output":
                if not attempts:
                    attempts.append({"id": "1", "status": "failed", "prompt":"", "response": "", "instance_name": category})
                
                last_attempt = attempts[-1]
                last_attempt["response"] = extract_block_content(body)
                metrics = parse_metrics_table(body)
                last_attempt.update(metrics)
        
        idx += 5
    
    # Transform map to final list structure
    # gen_map is { "1": { "edge": {...}, "baseline": {...} } }
    
    # Sort generation IDs numerically if possible
    sorted_gen_ids = sorted(gen_map.keys(), key=lambda x: int(x) if x.isdigit() else x)
    
    for gen_id in sorted_gen_ids:
        categories_map = gen_map[gen_id]
        categories_list = []
        
        # Sort categories alphabetically or by some order? Alphabetical is fine.
        for cat_name in sorted(categories_map.keys()):
            cat_data = categories_map[cat_name]
            
            # Update last attempt status to success
            attempts = cat_data["IListInstantiator"]["attempts"]
            if attempts:
                attempts[-1]["status"] = "success"
                
            categories_list.append(cat_data)
            
        experiment["generations"].append({
            "id": gen_id,
            "categories": categories_list
        })

    experiment["number_generations"] = len(experiment["generations"])
    
    # Global summary tokens aggregation
    grand_total_in = analyzer_data["input_tokens"]
    grand_total_out = analyzer_data["output_tokens"]
    grand_total_time = analyzer_data["time_seconds"]
    
    for gen in experiment["generations"]:
        for cat in gen["categories"]:
            # Creator
            c = cat["IListCreator"]
            grand_total_in += c["input_tokens"]
            grand_total_out += c["output_tokens"]
            grand_total_time += c["time_seconds"]
            
            # Instantiator attempts
            for a in cat["IListInstantiator"]["attempts"]:
                grand_total_in += a.get("input_tokens", 0)
                grand_total_out += a.get("output_tokens", 0)
                grand_total_time += a.get("time_seconds", 0)
            
    experiment["input_tokens"] = grand_total_in
    experiment["output_tokens"] = grand_total_out
    experiment["total_tokens"] = grand_total_in + grand_total_out
    experiment["time_seconds"] = round(grand_total_time, 2)


def parse_simple_log(content, experiment):
    """
    Original parsing logic for Simple experiments.
    """
    # Extract System Prompt
    system_prompt_match = re.search(r'SystemMessage \{ text = "(.*?)" \}', content, re.DOTALL)
    if system_prompt_match:
        experiment["system_prompt"] = system_prompt_match.group(1).replace(r'\n', '\n')

    # Split by Generation
    # Regex: # Generation <id>
    parts = re.split(r'^# Generation (\d+(\.\d+)?)', content, flags=re.MULTILINE)
    
    # Dictionary to hold generations by base ID (e.g. "5")
    gen_groups = {} 
    
    # parts[0] is preamble
    # loop: id, suffix(optional), body
    i = 1
    while i < len(parts):
        full_id_str = parts[i]
        # parts[i+1] is the capturing group for (\.\d+), might be None or string
        body = parts[i+2]
        
        # Determine base ID and attempt index
        if '.' in full_id_str:
            base_id, sub_id = full_id_str.split('.')
            attempt_idx = int(sub_id)
        else:
            base_id = full_id_str
            attempt_idx = 0 # 0-indexed for logic
            
        # Parse Metrics
        metrics = parse_metrics_table(body)
        
        # Parse Prompt & Response
        prompt = ""
        user_msg = re.search(r'UserMessage .*? text = "(.*?)"', body, re.DOTALL)
        if user_msg: prompt = user_msg.group(1).replace(r'\n', '\n')
        
        response = extract_block_content(body)
        
        instance_name = "output"
        
        gen_data = {
            "prompt": prompt,
            "response": response,
            "instance_name": instance_name,
            **metrics
        }
        
        if base_id not in gen_groups:
            gen_groups[base_id] = []
        gen_groups[base_id].append((attempt_idx, gen_data))
        
        i += 3
        
    # Construct final Objects
    for base_id, group in gen_groups.items():
        # Sort by attempt index
        group.sort(key=lambda x: x[0])
        
        attempts_list = []
        for idx, (original_idx, raw_gen) in enumerate(group):
             is_last = (idx == len(group) - 1)
             status = "success" if is_last else "failed"
             
             attempt_obj = {
                 "id": str(idx + 1),
                 "status": status,
                 "prompt": raw_gen["prompt"],
                 "response": raw_gen["response"],
                 "instance_name": raw_gen["instance_name"],
                 "input_tokens": raw_gen["input_tokens"],
                 "output_tokens": raw_gen["output_tokens"],
                 "total_tokens": raw_gen["total_tokens"],
                 "finish_reason": raw_gen["finish_reason"],
                 "time_seconds": raw_gen["time_seconds"]
             }
             attempts_list.append(attempt_obj)
             
        experiment["generations"].append({
            "id": base_id,
            "attempts": attempts_list
        })
        
    experiment["number_generations"] = len(experiment["generations"])
    
    # Parse Summary totals
    summary_matches = re.search(r'# Summary for all generations\n(.*?)$', content, re.DOTALL)
    if summary_matches:
        text = summary_matches.group(1)
        # Reuse helper, though keys are slightly different in summary text
        it = re.search(r'Sum of input tokens:\s*(\d+)', text)
        ot = re.search(r'Sum of output tokens:\s*(\d+)', text)
        tt = re.search(r'Sum of total tokens:\s*(\d+)', text)
        ts = re.search(r'Generations time:\s*([\d\.]+)', text)
        
        if it: experiment["input_tokens"] = int(it.group(1))
        if ot: experiment["output_tokens"] = int(ot.group(1))
        if tt: experiment["total_tokens"] = int(tt.group(1))
        if ts: experiment["time_seconds"] = float(ts.group(1))

def parse_log_file(file_path):
    """
    Parses a logs.md file and returns a dictionary representing the experiment.
    Detects type (simple vs cot) and dispatches to appropriate parser.
    """
    path_parts = file_path.split(os.sep)
    try:
        # Expected path structure: .../instances/<Type>/<Domain>/<Model>/<Timestamp>/logs.md
        timestamp_dir = path_parts[-2]
        model_name = path_parts[-3]
        domain_name = path_parts[-4]
        exp_type_dir = path_parts[-5]

        # Determine normalized type
        if "cot" in exp_type_dir.lower():
            exp_type = "cot"
        else:
            exp_type = "simple"
            
        experiment_id = f"{exp_type.capitalize()}-{domain_name}-{timestamp_dir}"
        
        # Base experiment structure
        experiment = {
            "id": experiment_id,
            "type": exp_type,
            "date": timestamp_dir.split("--")[0],
            "time": timestamp_dir.split("--")[1].replace("-", ":"),
            "model": {
                "name": model_name,
                "provider": "unknown",
                "version": "unknown"
            },
            "domain_name": domain_name,
            "number_generations": 0,
            "generations": []
        }

        # Parse content
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()

        if exp_type == "cot":
            parse_cot_log(content, experiment)
        else:
            parse_simple_log(content, experiment)

        return experiment

    except Exception as e:
        print(f"Error parsing log file {file_path}: {e}")
        return None

if __name__ == "__main__":
    # Auto-detect target log based on files discussed or fallback
    # The user provided a CoT log: /home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/bank/GPT_4O/24-03-2025--21-13-02/logs.md
    
    # target_log = "/home/aac/Repos/llm-instancer/src/main/resources/instances/CoT/bank/GPT_4O/24-03-2025--21-13-02/logs.md" 
    target_log = "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/bank/GPT_4O/21-03-2025--15-41-00/logs.md"
    
    if os.path.exists(target_log):
        result = parse_log_file(target_log)
        
        if result:
            output_path = "experiments_data.json"
            
            # Wrap in "experiments" list
            final_output = {"experiments": [result]}
            
            with open(output_path, 'w', encoding='utf-8') as f:
                json.dump(final_output, f, indent=2)
                
            print(f"Successfully converted logs to {output_path}")
    else:
        print(f"File not found: {target_log}")
