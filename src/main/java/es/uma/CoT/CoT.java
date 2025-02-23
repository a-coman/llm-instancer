package es.uma.CoT;

import es.uma.Experiment;
import es.uma.Llms;
import es.uma.Use;
import es.uma.Utils;
import dev.langchain4j.model.chat.ChatLanguageModel;
// Log4j
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

public class CoT {
    public static void run(Experiment experiment) {

        // Initialize AImodel and agents
        ChatLanguageModel AImodel = Llms.getModel(experiment.model);
        IModelAnalyzer modelAnalyzer = Llms.getAgent(IModelAnalyzer.class, AImodel);
        IListCreator listCreator = Llms.getAgent(IListCreator.class, AImodel);
        IListInstantiator listInstantiator = Llms.getAgent(IListInstantiator.class, AImodel);

        // Load category prompts
        final CategoryPrompts CATEGORY_PROMPTS = new CategoryPrompts();
        
        // Read modelUML and exampleSOIL
        String modelUML = Utils.readFile(experiment.umlPath); 
        String exampleSOIL = Utils.readFile(experiment.examplePath);
        Use use = new Use();

        // Create class diagram modelDescription in plain English
        String modelDescription = modelAnalyzer.chat(modelUML);
        Utils.saveFile(modelDescription, experiment.instancePath, "output.md");

        // For each category, create instances
        CATEGORY_PROMPTS.list.forEach( (categoryId, categoryPrompt) -> {
            
            // Create list
            String list = listCreator.chat(categoryPrompt, modelDescription);
            Utils.saveFile("\n\n" + categoryPrompt + list, experiment.instancePath, "output.md");

            // Create SOIL
            String instanceSOIL = listInstantiator.chat(list, exampleSOIL);
            Utils.saveFile(instanceSOIL, experiment.instancePath, "temp.soil", false);
            
            // Check syntax
            use.checkSyntax(experiment.umlPath, experiment.instancePath + "temp.soil");
            
            if (!categoryId.equals("invalid")) { // Check invariants/multiplicities only for valid instances
                String check = use.checkRestrictions(experiment.umlPath, experiment.instancePath + "temp.soil", modelDescription.substring(modelDescription.indexOf("Invariants")));  
                int numberOfChecks = 1;

                while (check != "OK" && numberOfChecks < 3) { // If the check is not OK, try again (MAX:2)
                    instanceSOIL = listInstantiator.chat("The list and output is partially incorrect: \n" + check + "\n Please provide the corrected full output");    
                    check = use.checkRestrictions(experiment.umlPath, experiment.instancePath + "temp.soil", modelDescription.substring(modelDescription.indexOf("Invariants")));  
                    numberOfChecks++;
                }

                Utils.saveFile(instanceSOIL + "\n\n", experiment.instancePath, "outputValid.soil");
            } else {
                Utils.saveFile(instanceSOIL + "\n\n", experiment.instancePath, "outputInvalid.soil");
            }
            
            Utils.saveFile(instanceSOIL + "\n\n", experiment.instancePath, "output.soil");
            Utils.saveFile(instanceSOIL + "\n\n", experiment.instancePath, categoryId + ".soil");
            Utils.saveFile("\n```\n" + instanceSOIL + "\n```\n", experiment.instancePath, "output.md");

        });

        use.close();
    }

}