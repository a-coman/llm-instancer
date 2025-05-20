package es.uma.CoT;

import es.uma.Experiment;
import es.uma.Listener;
import es.uma.Llms;
import es.uma.Use;
import es.uma.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
// Log4j
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

public class CoT {

    private static final int MAX_ATTEMPTS = 2;

    public static void run(Experiment experiment) {
        
        // Load propmts, get modelUML, exampleSOIL and use shell
        final CategoryPrompts CATEGORY_PROMPTS = new CategoryPrompts(experiment.getSizePrompt());
        final String modelUML = Utils.readFile(experiment.getUmlPath()); 
        final String exampleSOIL = Utils.readFile(experiment.getExamplePath());
        Use use = new Use();

        // Create class diagram modelDescription in plain English
        IModelAnalyzer modelAnalyzer = Llms.getAgent(IModelAnalyzer.class, experiment.getModel());
        String modelDescription = modelAnalyzer.chat(modelUML);
        String invariants = modelDescription.substring(modelDescription.indexOf("Invariants"));

        // For each category create lists with threads and add to queue
        BlockingQueue<List> queue = new LinkedBlockingQueue<>();
        
        CATEGORY_PROMPTS.list.forEach( (categoryId, categoryPrompt) -> {
            ListCreator listCreator = new ListCreator(experiment, categoryId, categoryPrompt, modelDescription, queue);
            Thread thread = new Thread(listCreator);
            thread.start();         
        });
        
        // Instantiate all generated lists
        IListInstantiator listInstantiator = Llms.getAgent(IListInstantiator.class, experiment.getModel());
        int totalLists = CATEGORY_PROMPTS.list.size() * experiment.getRepetitions();
        
        for(int i = 0; i < totalLists; i++) {
            List list = getListFromQueue(queue);
            instantiateList(modelUML, i, list, listInstantiator, experiment, use, invariants, exampleSOIL);
        }

        use.close();
    }

    private static List getListFromQueue(BlockingQueue<List> queue) {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for list", e);
        }
    }

    private static void instantiateList(String modelUML, int i, List list, IListInstantiator listInstantiator, Experiment experiment, Use use, String invariants, String exampleSOIL) {
        Listener.setCurrentCategory(list.id() + list.gen());
        // Generate and save temp instance
        String instancePath = experiment.getInstancePath() + "gen" + list.gen() + "/";
        String instanceSOIL;
        
        if (i == 0) {
            instanceSOIL = listInstantiator.chat(modelUML, exampleSOIL, list.value());    
        } else {
            instanceSOIL = listInstantiator.chat("Continue with the following description, creating the instance according to the syntax example and this specification: \n" + list.value()); 
        }
        
        Utils.saveFile(Utils.removeComments(instanceSOIL), instancePath, "temp.soil", false);
        
        // We check syntax for all
        String syntaxErrors;
        int attempts = 0;
        final int MAX_ATTEMPTS = 2;

        do {
            syntaxErrors = use.checkSyntax(experiment.getUmlPath(), instancePath + "temp.soil");
            if (!syntaxErrors.equals("OK")) {
                instanceSOIL = listInstantiator.chat("The last output is partially incorrect: \n" + syntaxErrors + "\n\nPlease provide the complete output corrected");
                Utils.saveFile(Utils.removeComments(instanceSOIL), instancePath, "temp.soil", false);
            }
            attempts++;
        } while (!syntaxErrors.equals("OK") && attempts < MAX_ATTEMPTS);
        
        // We check multiplicities and invariants only for valid lists
        if (!list.id().contains("invalid")) {
            instanceSOIL = validateConstraints(instanceSOIL, listInstantiator, list, experiment, use, invariants);
            Utils.saveFile(Utils.removeComments(instanceSOIL) + "\n\n", instancePath, "outputValid.soil");
        } else {
            Utils.saveFile(Utils.removeComments(instanceSOIL) + "\n\n", instancePath, "outputInvalid.soil");
        }
        
        String processedInstance = Utils.removeComments(instanceSOIL);
        Utils.saveFile(processedInstance + "\n\n", instancePath, "output.soil");
        Utils.saveFile(processedInstance, instancePath, list.id() + ".soil");
    }

    private static String validateConstraints(String instanceSOIL, IListInstantiator listInstantiator, List list, Experiment experiment, Use use, String invariants) {
        
        String instancePath = experiment.getInstancePath() + "gen" + list.gen() + "/";
        String multiplicitiesErrors;
        String invariantsErrors;
        String check;
        int attempts = 0;

        do {
            multiplicitiesErrors = use.checkMultiplicities(experiment.getUmlPath(), instancePath + "temp.soil");
            invariantsErrors = use.checkInvariants(experiment.getUmlPath(), instancePath + "temp.soil", invariants);

            multiplicitiesErrors = multiplicitiesErrors.equals("OK") ? "" : multiplicitiesErrors + "\n";
            invariantsErrors = invariantsErrors.equals("OK") ? "" : invariantsErrors;
            check = (multiplicitiesErrors + invariantsErrors).trim();

            if (!check.isEmpty()) {
                instanceSOIL = listInstantiator.chat("The last output is partially incorrect: \n" + check + "\n\nPlease provide the complete output corrected");    
                Utils.saveFile(Utils.removeComments(instanceSOIL), instancePath, "temp.soil", false);
            }

            attempts++;
        } while (!check.isEmpty() && attempts < MAX_ATTEMPTS);

        return instanceSOIL;
    }

}