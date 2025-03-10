package es.uma.Simple;

import es.uma.Experiment;
import es.uma.Listener;
import es.uma.Llms;
import es.uma.Utils;

public class Simple {
    public static void run(Experiment experiment) {
        ISimple simple = Llms.getAgent(ISimple.class, experiment.model);

        String modelUML = Utils.readFile(experiment.umlPath); 
        String exampleSOIL = Utils.readFile(experiment.examplePath);

        for (int gen = 1; gen <= experiment.repetitions; gen++) {
            String instancePath = experiment.instancePath + "gen" + gen + "/"; 
            Listener.logsPath = instancePath;
            String response;
            
            if (gen == 1) {
                response = simple.chat(modelUML, exampleSOIL);
            } else {
                response = simple.chat("Let's continue generating more instances that are structurally and semantically different from the previous ones");
            }

            Utils.saveFile(Utils.removeComments(response), instancePath, "output.soil");
            
            //Utils.waitFor(1); // To avoid rate limiting 
        }
    }
}
