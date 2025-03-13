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
    public static void run(Experiment experiment) {
        
        // Load propmts, get modelUML, exampleSOIL and use shell
        final CategoryPrompts CATEGORY_PROMPTS = new CategoryPrompts();
        final String modelUML = Utils.readFile(experiment.umlPath); 
        final String exampleSOIL = Utils.readFile(experiment.examplePath);
        Use use = new Use();

        // Create class diagram modelDescription in plain English
        IModelAnalyzer modelAnalyzer = Llms.getAgent(IModelAnalyzer.class, experiment.model);
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
        IListInstantiator listInstantiator = Llms.getAgent(IListInstantiator.class, experiment.model);
        int totalLists = CATEGORY_PROMPTS.list.size() * experiment.repetitions;
        
        for(int i = 0; i < totalLists; i++) {
            List list = getListFromQueue(queue);
            instantiateList(list, listInstantiator, experiment, use, invariants, exampleSOIL);
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

    private static void instantiateList(List list, IListInstantiator listInstantiator, Experiment experiment, Use use, String invariants, String exampleSOIL) {
        Listener.setCurrentCategory(list.id());
        // Generate and save temp instance
        String instancePath = experiment.instancePath + list.gen() + "/";
        String instanceSOIL = listInstantiator.chat(list.value(), exampleSOIL);
        Utils.saveFile(Utils.removeComments(instanceSOIL), instancePath, "temp.soil", false);
        
        // We check syntax for all
        String syntaxErrors;
        int attempts = 0;
        final int MAX_ATTEMPTS = 2;

        do {
            syntaxErrors = use.checkSyntax(experiment.umlPath, instancePath + "temp.soil");
            if (!syntaxErrors.equals("OK")) {
                instanceSOIL = listInstantiator.chat("The last output had some syntax errors: \n " + syntaxErrors + "\n Please provide it corrected");
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
        
        String instancePath = experiment.instancePath + list.gen() + "/";
        String multiplicitiesErrors;
        String invariantsErrors;
        String check;
        int attempts = 0;
        final int MAX_ATTEMPTS = 2;

        do {
            multiplicitiesErrors = use.checkMultiplicities(experiment.umlPath, instancePath + "temp.soil");
            invariantsErrors = use.checkInvariants(experiment.umlPath, instancePath + "temp.soil", invariants);

            multiplicitiesErrors = multiplicitiesErrors.equals("OK") ? "" : multiplicitiesErrors + "\n";
            invariantsErrors = invariantsErrors.equals("OK") ? "" : invariantsErrors;
            check = multiplicitiesErrors + invariantsErrors;

            if (!check.isEmpty()) {
            instanceSOIL = listInstantiator.chat(
                "The last output is partially incorrect: \n" + check + 
                "\n Please provide it corrected");    
            Utils.saveFile(Utils.removeComments(instanceSOIL), 
                instancePath, "temp.soil", false);
            }

            attempts++;
        } while (!check.isEmpty() && attempts < MAX_ATTEMPTS);

        return instanceSOIL;
    }

}