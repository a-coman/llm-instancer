import re
import json
import os
from datetime import datetime

def parse_logs_markdown(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Extract metadata from file path
    path_parts = file_path.split(os.sep)
    try:
        timestamp_dir = path_parts[-2]
        model_name_dir = path_parts[-3]
        domain_name = path_parts[-4]
        type_name = path_parts[-5]
        
        date_obj = datetime.strptime(timestamp_dir, "%d-%m-%Y--%H-%M-%S")
        date_str = date_obj.strftime("%Y-%m-%d")
        time_str = date_obj.strftime("%H:%M:%S")
    except Exception:
        timestamp_dir = "unknown"
        model_name_dir = "unknown" 
        domain_name = "unknown"
        type_name = "unknown"
        date_str = "unknown"
        time_str = "unknown"

    # Base experiment metadata
    experiment_metadata = {
        "id": f"{type_name}-{domain_name}-{timestamp_dir}",
        "type": type_name.lower(),
        "date": date_str,
        "time": time_str,
        "model": {
            "name": model_name_dir,
            "provider": "unknown",
            "version": "unknown"
        },
        "domain_name": domain_name,
    }

    # Split by "# Input " headers
    chunks = re.split(r'^# Input ', content, flags=re.MULTILINE)[1:]

    raw_generations = []
    first_system_prompt = None

    # First Pass: Parsing Raw Data
    for chunk in chunks:
        header_line = chunk.split('\n', 1)[0].strip()
        # header_line example: "ISimple : gen1"
        
        # Extract System Prompt
        sys_match = re.search(r'SystemMessage \{ text = "(.*?)" \}', chunk, re.DOTALL)
        if sys_match:
            current_sys_prompt = sys_match.group(1).replace('\\"', '"').replace('\n', ' ')
            if first_system_prompt is None:
                first_system_prompt = current_sys_prompt

        # Extract User Prompt
        user_match = re.search(r'UserMessage \{.*?text = "(.*?)" \}\] \}', chunk, re.DOTALL)
        prompt_text = user_match.group(1) if user_match else ""

        # Extract Response
        output_match = re.search(r'# Output.*?\n```(.*?)```', chunk, re.DOTALL)
        response_text = output_match.group(1).strip() if output_match else ""

        # Extract Metrics
        finish_reason = re.search(r'Finish Reason:\s*(\w+)', chunk)
        input_tokens = re.search(r'Input Tokens:\s*(\d+)', chunk)
        output_tokens = re.search(r'Output Tokens:\s*(\d+)', chunk)
        total_tokens = re.search(r'Total Tokens:\s*(\d+)', chunk)
        time_seconds = re.search(r'Generation Time:\s*([\d\.]+)', chunk)

        # Extract Generation Number for internal grouping
        gen_num_match = re.search(r'gen(\d+)', header_line)
        gen_num = gen_num_match.group(1) if gen_num_match else "0"

        # Determine Display Instance Name
        if type_name.lower() == "simple":
            display_instance_name = "output"
        else:
            display_instance_name = header_line

        gen_data = {
            "prompt": prompt_text.replace('\\n', '\n'),
            "response": response_text,
            "instance_name": display_instance_name,
            "_gen_num": gen_num, # Internal use only
            "input_tokens": int(input_tokens.group(1)) if input_tokens else 0,
            "output_tokens": int(output_tokens.group(1)) if output_tokens else 0,
            "total_tokens": int(total_tokens.group(1)) if total_tokens else 0,
            "finish_reason": finish_reason.group(1).lower() if finish_reason else "unknown",
            "time_seconds": float(time_seconds.group(1)) if time_seconds else 0.0
        }
        raw_generations.append(gen_data)

    # Second Pass: Processing Retries and ID assignment
    processed_generations = []
    
    # Identify groups using the raw generation number
    groups = {} # map "5" -> [index1, index2]
    group_order = [] 
    
    for idx, gen in enumerate(raw_generations):
        gen_num = gen["_gen_num"]
        if gen_num not in groups:
            groups[gen_num] = []
            group_order.append(gen_num)
        groups[gen_num].append(idx)

    # Convert groups into generation objects with nested attempts
    for gen_num in group_order:
        group_indices = groups[gen_num]
        
        attempts_list = []
        for attempt_idx, idx in enumerate(group_indices):
            raw_gen = raw_generations[idx]
            
            # This is not really how it works, but for this already made experiments
            # we can just consider the last attempt as successful
            # and the rest as failed
            # in the feature we will have (success | failed | max_attemps)
            # Determine status: "success" if it's the last attempt, else "failed"
            is_last = (attempt_idx == len(group_indices) - 1)
            status = "success" if is_last else "failed"
            
            # Simple string ID for the attempt ("1", "2", "3"...)
            attempt_obj = {
                "id": str(attempt_idx + 1),
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
        
        gen_obj = {
            "id": gen_num,         # "1", "5", etc.
            "attempts": attempts_list
        }
        
        processed_generations.append(gen_obj)

    # Parse summary metrics from the end of the file
    summary_match = re.search(r'# Summary for all generations(.*)', content, re.DOTALL)
    summary_data = {}
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

    # Construct final experiment object
    final_experiment = {
        "id": experiment_metadata["id"],
        "type": experiment_metadata["type"],
        "date": experiment_metadata["date"],
        "time": experiment_metadata["time"],
        "model": experiment_metadata["model"],
        "domain_name": experiment_metadata["domain_name"],
        "system_prompt": first_system_prompt if first_system_prompt else "",
        "number_generations": len(groups),
        "input_tokens": summary_data.get("input_tokens", 0),
        "output_tokens": summary_data.get("output_tokens", 0),
        "total_tokens": summary_data.get("total_tokens", 0),
        "time_seconds": summary_data.get("time_seconds", 0.0),
        "generations": processed_generations
    }

    return {"experiments": [final_experiment]}

if __name__ == "__main__":
    target_file = "/home/aac/Repos/llm-instancer/src/main/resources/instances/Simple/bank/GPT_4O/21-03-2025--15-41-00/logs.md"
    
    if os.path.exists(target_file):
        result = parse_logs_markdown(target_file)
        
        output_path = "experiments_data.json"
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(result, f, indent=2)
            
        print(f"Successfully converted logs to {output_path}")
    else:
        print(f"File not found: {target_file}")
