package es.uma.CoT;

import es.uma.Llms;
import es.uma.Use;
import es.uma.Utils;
import dev.langchain4j.model.chat.ChatLanguageModel;
// Log4j
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

public class Main {
    public static void main(String[] args) {

        // Initialize the agents
        ChatLanguageModel AImodel = Llms.getModel("4o");

        IModelAnalyzer modelAnalyzer = Llms.getAgent(IModelAnalyzer.class, AImodel);
        IListCreator listCreator = Llms.getAgent(IListCreator.class, AImodel);
        IListInstantiator listInstantiator = Llms.getAgent(IListInstantiator.class, AImodel);
        
        // Load constants
        final CategoryPrompts CATEGORY_PROMPTS = new CategoryPrompts();
        final String CURRENT_TIME = Utils.getTime();
        final String DIAGRAM = "hammers";
        final String INSTACE_PATH = "./src/main/resources/instances/" + DIAGRAM + "/" + CURRENT_TIME + "/";
        final String PROMPT_PATH = "./src/main/resources/prompts/" + DIAGRAM + "/";
        
        // Read model and example
        String modelUML = Utils.readFile(PROMPT_PATH + "diagram.use"); 
        String exampleSOIL = Utils.readFile(PROMPT_PATH + "examples/example_1.soil");
        Use use = new Use();

        // Create class diagram modelDescription in plain English
        String modelDescription = modelAnalyzer.chat(modelUML);
        Utils.saveFile(modelDescription, INSTACE_PATH, "output.md");

        // For each category, create instances
        CATEGORY_PROMPTS.list.forEach( (categoryId, categoryPrompt) -> {
            
            // Create list
            String list = listCreator.chat(categoryPrompt, modelDescription);
            Utils.saveFile("\n\n" + categoryPrompt + list, INSTACE_PATH, "output.md");

            // Create SOIL and check constraints
            String instanceSOIL = listInstantiator.chat(list, exampleSOIL);
            Utils.saveFile(instanceSOIL, INSTACE_PATH, "temp.soil", false);
            if (!categoryId.equals("invalid")) { // Check only for valid instances
                String check = use.check(PROMPT_PATH + "diagram.use", INSTACE_PATH + "temp.soil", modelDescription.substring(modelDescription.indexOf("Invariants")));  
                
                if (check != "OK")
                    instanceSOIL = listInstantiator.chat("The list and output is partially incorrect: \n" + check + "\n Please provide the corrected full output");    

                Utils.saveFile(instanceSOIL + "\n\n", INSTACE_PATH, "outputValid.soil");
            } else {
                Utils.saveFile(instanceSOIL + "\n\n", INSTACE_PATH, "outputInvalid.soil");
            }
            
            Utils.saveFile(instanceSOIL + "\n\n", INSTACE_PATH, "output" + ".soil");
            Utils.saveFile(instanceSOIL + "\n\n", INSTACE_PATH, categoryId + ".soil");
            Utils.saveFile("\n" + "```\n" + instanceSOIL + "\n```", INSTACE_PATH, "output.md");

        });

        use.close();
    }

}