package es.uma.Simple;

import es.uma.Experiment;
import es.uma.Llms;
import es.uma.Use;
import es.uma.Utils;

public class Simple {
    public static void run(Experiment experiment) {
        Use use = new Use();
        ISimple simple = Llms.getAgent(ISimple.class, experiment.model);

        String modelUML = Utils.readFile(experiment.umlPath); 
        String exampleSOIL = Utils.readFile(experiment.examplePath);

        for (int i = 1; i <= experiment.repetitions; i++) {
            String response;
            if (i == 1) {
                response = simple.chat(modelUML, exampleSOIL);
            } else {
                response = simple.chat("Let's continue generating more instances that are structurally and semantically different from the previous ones");
            }

            Utils.saveFile(Utils.removeComments(response), experiment.instancePath, "gen" + i + ".soil");
            Utils.saveFile(Utils.removeComments(response) + "\n", experiment.instancePath, "output.soil");
            use.checkSyntax(experiment.umlPath, experiment.instancePath + "gen" + i + ".soil");
            use.checkRestrictions(experiment.umlPath, experiment.instancePath + "gen" + i + ".soil", "");
            
            //Utils.waitFor(1); // To avoid rate limiting 
        }
    }
}
