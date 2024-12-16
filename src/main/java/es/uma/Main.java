package es.uma;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel.GoogleAiEmbeddingModelBuilder;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        // // Initialize the agents
        // ChatLanguageModel chatGPT = OpenAiChatModel.builder()
        //     .apiKey(System.getenv("OPEN_API_KEY"))
        //     .modelName("gpt-4o")
        //     .build();

        // Initialize the agents
        ChatLanguageModel AImodel = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GEMINI_API_KEY"))
            .modelName("gemini-1.5-flash")
            .logRequestsAndResponses(true)
            .build();

        IModelAnalyzer modelAnalyzer = AiServices.builder(IModelAnalyzer.class)
            .chatLanguageModel(AImodel)
            .build();

        ChatMemory listCreatorMemory = MessageWindowChatMemory.withMaxMessages(24);
        IListCreator listCreator = AiServices.builder(IListCreator.class)
            .chatLanguageModel(AImodel)
            .chatMemory(listCreatorMemory)
            .build();

        ChatMemory instantiatorMemory = MessageWindowChatMemory.withMaxMessages(24);
        IModelInstantiator modelInstantiator = AiServices.builder(IModelInstantiator.class)
            .chatLanguageModel(AImodel)
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
            String list = listCreator.chat(categoryDescription, modelDescription);
            Utils.saveFile("\n\n" + category + "\n" + list, "./src/main/resources/instances/" + diagram + currentTime, "/output.md");

            // Create SOIL and check constraints
            String instanceSOIL = modelInstantiator.chat(list, exampleSOIL);
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