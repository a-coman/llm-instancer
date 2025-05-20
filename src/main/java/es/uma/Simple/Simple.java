package es.uma.Simple;


import es.uma.Experiment;
import es.uma.Listener;
import es.uma.Llms;
import es.uma.Use;
import es.uma.Utils;

public class Simple {
    
    private static final int MAX_ATTEMPTS = 2;

    public static void run(Experiment experiment) {
        ISimple simple = Llms.getAgent(ISimple.class, experiment.getModel());

        String modelUML = Utils.readFile(experiment.getUmlPath()); 
        String exampleSOIL = Utils.readFile(experiment.getExamplePath());

        for (int gen = 1; gen <= experiment.getRepetitions(); gen++) {
            Listener.setCurrentCategory("gen" + gen);
            String instancePath = experiment.getInstancePath() + "gen" + gen + "/"; 
            String instanceSOIL;
            
            if (gen == 1) {
                instanceSOIL = simple.chat(modelUML, exampleSOIL, experiment.getSizePrompt());
            } else {
                instanceSOIL = simple.chat("Please generate another instance" + experiment.getSizePrompt() + " that is structurally and semantically different from the previous ones.");
            }

            Utils.saveFile(Utils.removeComments(instanceSOIL), instancePath, "output.soil", false);

            // Checks            
            int attempts = 0;
            String syntaxErrors, multiplicitiesErrors, invariantsErrors, check;
            Use use = new Use();

            // Check syntax
            do {
                syntaxErrors = use.checkSyntax(experiment.getUmlPath(), instancePath + "output.soil");
                if (!syntaxErrors.equals("OK")) {
                    instanceSOIL = simple.chat("The last output is partially incorrect: \n" + syntaxErrors + "\n\nPlease provide the complete output corrected");
                    Utils.saveFile(Utils.removeComments(instanceSOIL), instancePath, "output.soil", false);
                }
                attempts++;
            } while (!syntaxErrors.equals("OK") && attempts < MAX_ATTEMPTS);

            // Check multiplicities and invariants
            attempts = 0;
            do {
                multiplicitiesErrors = use.checkMultiplicities(experiment.getUmlPath(), instancePath + "output.soil");
                invariantsErrors = use.checkInvariants(experiment.getUmlPath(), instancePath + "output.soil", "");
                multiplicitiesErrors = multiplicitiesErrors.equals("OK") ? "" : multiplicitiesErrors + "\n";
                invariantsErrors = invariantsErrors.equals("OK") ? "" : invariantsErrors;
                check = multiplicitiesErrors + invariantsErrors;

                if (!check.isEmpty()) {
                    instanceSOIL = simple.chat("The last output is partially incorrect: \n" + check + "\n\nPlease provide the complete output corrected");    
                    Utils.saveFile(Utils.removeComments(instanceSOIL), instancePath, "output.soil", false);
                }
                attempts++;
            } while (!check.isEmpty() && attempts < MAX_ATTEMPTS);

            //Utils.waitFor(1); // To avoid rate limiting 
        }
    }
}
