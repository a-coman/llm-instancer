package es.uma;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class Main {

    private static IModelAnalyzer modelAnalyzer;
    private static IListCreator listCreator;
    private static IModelInstantiator modelInstantiator;

    private static void initializeAgents() {
        ChatLanguageModel chatGPT = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPEN_API_KEY"))
                .modelName("gpt-4o")
                .build();

        ChatMemory analyzerMemory = MessageWindowChatMemory.withMaxMessages(10);
        modelAnalyzer = AiServices.builder(IModelAnalyzer.class)
                .chatLanguageModel(chatGPT)
                .chatMemory(analyzerMemory)
                .build();

        ChatMemory listCreatorMemory = MessageWindowChatMemory.withMaxMessages(10);
        listCreator = AiServices.builder(IListCreator.class)
                .chatLanguageModel(chatGPT)
                .chatMemory(listCreatorMemory)
                .build();

        ChatMemory instantiatorMemory = MessageWindowChatMemory.withMaxMessages(10);
        modelInstantiator = AiServices.builder(IModelInstantiator.class)
                .chatLanguageModel(chatGPT)
                .chatMemory(instantiatorMemory)
                .build();
    }

    public static void main(String[] args) {
        initializeAgents();
        Prompts prompts = new Prompts();
        String model = "bank/";
        String modelUML = Utils.readFile("./src/main/resources/prompts/" + model + "diagram.use"); 
        String exampleSOIL = Utils.readFile("./src/main/resources/prompts/" + model + "examples/example_1.soil");
        String currentTime = Utils.getTime();
        Use use = new Use();

        // Create model description in plain english
        String description = modelAnalyzer.chat(modelUML);
        Utils.saveFile(description, "./src/main/resources/instances/" + model + currentTime, "/output.md");

        // For each category, create instances
        prompts.list.forEach( (category, categoryDescription) -> {
            
            // Create list
            String instance = listCreator.chat(categoryDescription + description);
            Utils.saveFile("\n\n" + category + "\n" + instance, "./src/main/resources/instances/" + model + currentTime, "/output.md");

            // Create SOIL and check constraints
            String instanceSOIL = modelInstantiator.chat("Lets continue with the following instance: \n" + instance + "\n # Syntax exaple: \n" + exampleSOIL);
            Utils.saveFile(instanceSOIL, "./src/main/resources/instances/" + model + currentTime, "/temp.soil", false);
            if (!category.equals("# Category: Realistic but invalid")) { // Check only for valid instances
                String check = use.check("/home/andrei/Repos/llm-instancer/src/main/resources/prompts/" + model + "diagram.use", 
                                        "home/andrei/Repos/llm-instancer/src/main/resources/instances/" + model + currentTime + "/temp.soil"); 
                
                if (check != "OK")
                    instanceSOIL = modelInstantiator.chat(check);    

                Utils.saveFile(instanceSOIL, "./src/main/resources/instances/" + model + currentTime, "/valid.soil");
            } else {
                Utils.saveFile(instanceSOIL, "./src/main/resources/instances/" + model + currentTime, "/invalid.soil");
            }

            Utils.saveFile("\n" + "```\n" + instanceSOIL + "\n```", "./src/main/resources/instances/" + model + currentTime, "/output.md");

        });
    }

}