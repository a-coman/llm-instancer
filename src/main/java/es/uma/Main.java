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
        // #region Initialize the agents
        
        ChatLanguageModel AImodel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPEN_API_KEY"))
            .modelName("gpt-4o")
            .build();

        // ChatLanguageModel AImodel = GoogleAiGeminiChatModel.builder()
        //     .apiKey(System.getenv("GEMINI_API_KEY"))
        //     .modelName("gemini-1.5-flash")
        //     .logRequestsAndResponses(true)
        //     .build();

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

        // #endregion	
        
        // Load constants
        final Prompts PROMPTS = new Prompts();
        final String CURRENT_TIME = Utils.getTime();
        final String DIAGRAM = "hammers";
        final String INSTACE_PATH = "./src/main/resources/instances/" + DIAGRAM + "/" + CURRENT_TIME + "/";
        final String PROMPT_PATH = "./src/main/resources/prompts/" + DIAGRAM + "/";
        final String INVALID = "# Category: Realistic but invalid";
        
        // Read model and example
        String modelUML = Utils.readFile(PROMPT_PATH + "diagram.use"); 
        String exampleSOIL = Utils.readFile(PROMPT_PATH + "examples/example_1.soil");
        Use use = new Use();

        // Create class diagram modelDescription in plain English
        String modelDescription = modelAnalyzer.chat(modelUML);
        Utils.saveFile(modelDescription, INSTACE_PATH, "output.md");

        // For each category, create instances
        PROMPTS.list.forEach( (category, categoryDescription) -> {
            
            // Create list
            String list = listCreator.chat(categoryDescription, modelDescription);
            Utils.saveFile("\n\n" + category + "\n" + list, INSTACE_PATH, "output.md");

            // Create SOIL and check constraints
            String instanceSOIL = modelInstantiator.chat(list, exampleSOIL);
            Utils.saveFile(instanceSOIL, INSTACE_PATH, "temp.soil", false);
            if (!category.equals(INVALID)) { // Check only for valid instances
                String check = use.check(PROMPT_PATH + "diagram.use", INSTACE_PATH + "temp.soil", modelDescription.substring(modelDescription.indexOf("Invariants")));  
                
                if (check != "OK")
                    instanceSOIL = modelInstantiator.chat("The list and output is partially incorrect: \n" + check + "\n Please provide the corrected full output");    

                Utils.saveFile(instanceSOIL + "\n\n", INSTACE_PATH, "valid.soil");
            } else {
                Utils.saveFile(instanceSOIL + "\n\n", INSTACE_PATH, "invalid.soil");
            }

            Utils.saveFile("\n" + "```\n" + instanceSOIL + "\n```", INSTACE_PATH, "output.md");

        });

        use.close();
    }

}