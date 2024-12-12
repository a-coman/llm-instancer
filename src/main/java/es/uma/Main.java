package es.uma;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class Main {
    public static void main(String[] args) {
        
        // Initialize the agents
        ChatLanguageModel chatGPT = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPEN_API_KEY"))
            .modelName("gpt-4o")
            .build();

        IModelAnalyzer modelAnalyzer = AiServices.builder(IModelAnalyzer.class)
            .chatLanguageModel(chatGPT)
            .build();

        ChatMemory listCreatorMemory = MessageWindowChatMemory.withMaxMessages(24);
        IListCreator listCreator = AiServices.builder(IListCreator.class)
            .chatLanguageModel(chatGPT)
            .chatMemory(listCreatorMemory)
            .build();

        ChatMemory instantiatorMemory = MessageWindowChatMemory.withMaxMessages(24);
        IModelInstantiator modelInstantiator = AiServices.builder(IModelInstantiator.class)
            .chatLanguageModel(chatGPT)
            .chatMemory(instantiatorMemory)
            .build();

        // Load variables
        Prompts prompts = new Prompts();
        String diagram = "hammers/";
        String modelUML = Utils.readFile("./src/main/resources/prompts/" + diagram + "diagram.use"); 
        String exampleSOIL = Utils.readFile("./src/main/resources/prompts/" + diagram + "examples/example_1.soil");
        String currentTime = Utils.getTime();
        Use use = new Use();

        // Create class diagram modelDescription in plain english
        String modelDescription = modelAnalyzer.chat(modelUML);
        Utils.saveFile(modelDescription, "./src/main/resources/instances/" + diagram + currentTime, "/output.md");

        // For each category, create instances
        prompts.list.forEach( (category, categoryDescription) -> {
            
            // Create list
            String list = listCreator.chat(categoryDescription + "\n\n" + modelDescription);
            Utils.saveFile("\n\n" + category + "\n" + list, "./src/main/resources/instances/" + diagram + currentTime, "/output.md");

            // Create SOIL and check constraints
            String instanceSOIL = modelInstantiator.chat("Lets continue with the following list: \n" + list + "\n # Syntax exaple: \n" + exampleSOIL);
            Utils.saveFile(instanceSOIL, "./src/main/resources/instances/" + diagram + currentTime, "/temp.soil", false);
            if (!category.equals("# Category: Realistic but invalid")) { // Check only for valid instances
                String check = use.check("/home/andrei/Repos/llm-instancer/src/main/resources/prompts/" + diagram + "diagram.use", 
                "/home/andrei/Repos/llm-instancer/src/main/resources/instances/" + diagram + currentTime + "/temp.soil", modelDescription.substring(modelDescription.indexOf("Invariants"))); 
                
                if (check != "OK")
                    instanceSOIL = modelInstantiator.chat("The list and output is partially incorrect: \n" + check + "\n Please provide the corrected full output");    

                Utils.saveFile(instanceSOIL + "\n\n", "./src/main/resources/instances/" + diagram + currentTime, "/valid.soil");
            } else {
                Utils.saveFile(instanceSOIL + "\n\n", "./src/main/resources/instances/" + diagram + currentTime, "/invalid.soil");
            }

            Utils.saveFile("\n" + "```\n" + instanceSOIL + "\n```", "./src/main/resources/instances/" + diagram + currentTime, "/output.md");

        });

        use.close();
    }

}