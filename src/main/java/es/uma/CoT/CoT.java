package es.uma.CoT;

import es.uma.Experiment;
import es.uma.Llms;
import es.uma.Metrics;
import es.uma.Use;
import es.uma.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
// Log4j
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

public class CoT {
    public static void run(Experiment experiment) {

        // Initialize AImodel and agents
        IModelAnalyzer modelAnalyzer = Llms.getAgent(IModelAnalyzer.class, experiment.model);
        IListInstantiator listInstantiator = Llms.getAgent(IListInstantiator.class, experiment.model);

        // Load category prompts
        final CategoryPrompts CATEGORY_PROMPTS = new CategoryPrompts();
        
        // Get modelUML, exampleSOIL and use shell
        String modelUML = Utils.readFile(experiment.umlPath); 
        String exampleSOIL = Utils.readFile(experiment.examplePath);
        Use use = new Use();

        // Create class diagram modelDescription in plain English
        String modelDescription = modelAnalyzer.chat(modelUML);

        // For each category create list threads
        BlockingQueue<List> queue = new LinkedBlockingQueue<>();
        
        CATEGORY_PROMPTS.list.forEach( (categoryId, categoryPrompt) -> {
            ListCreator listCreator = new ListCreator(experiment, categoryId, categoryPrompt, modelDescription, queue);
            Thread thread = new Thread(listCreator);
            thread.start();         
        });
        
        // For each category take from queue and create SOIL 
        for(int i = 0; i < CATEGORY_PROMPTS.list.size() * experiment.repetitions; i++) {
            List list;

            try {
                list = queue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted", e);
            }
            
            String instanceSOIL = listInstantiator.chat(list.value(), exampleSOIL);
            Utils.saveFile(Utils.removeComments(instanceSOIL), experiment.instancePath, "temp.soil", false);
        
            // Check syntax
            String syntaxErrors = use.checkSyntax(experiment.umlPath, experiment.instancePath + "temp.soil");            
    
            // Check Restrictions (invariants/multiplicities)
            if (!list.id().contains("invalid")) { // only for valid instances
                String check = use.checkRestrictions(experiment.umlPath, experiment.instancePath + "temp.soil", modelDescription.substring(modelDescription.indexOf("Invariants")));  
                int numberOfChecks = 1;

                while (check != "OK" && numberOfChecks < 3) { // If the check is not OK, try again (MAX:2)
                    instanceSOIL = listInstantiator.chat("The list and output is partially incorrect: \n" + check + "\n Please provide the corrected full output");    
                    Utils.saveFile(Utils.removeComments(instanceSOIL), experiment.instancePath, "temp.soil", false);
                    check = use.checkRestrictions(experiment.umlPath, experiment.instancePath + "temp.soil", modelDescription.substring(modelDescription.indexOf("Invariants")));  
                    numberOfChecks++;
                }

                if (numberOfChecks == 3) { // Mark as failed
                    Metrics.addFailedCheck(list.id());
                }

                Utils.saveFile(Utils.removeComments(instanceSOIL) + "\n\n", experiment.instancePath, "outputValid.soil");
            } else {
                Utils.saveFile(Utils.removeComments(instanceSOIL) + "\n\n", experiment.instancePath, "outputInvalid.soil");
            }
            
            Utils.saveFile(Utils.removeComments(instanceSOIL) + "\n\n", experiment.instancePath, "output.soil");
            Utils.saveFile(Utils.removeComments(instanceSOIL) + "\n\n", experiment.instancePath, list.id() + ".soil");
            
        }

        use.close();
    }

}