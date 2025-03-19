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
            Listener.setCurrentCategory("gen" + gen);
            String instancePath = experiment.instancePath + "gen" + gen + "/"; 
            String response;
            
            if (gen == 1) {
                response = simple.chat(modelUML, exampleSOIL);
            } else {
                response = simple.chat("Please, generate another instance that is structurally and semantically different from the previous ones");
            }

            Utils.saveFile(Utils.removeComments(response), instancePath, "output.soil");
            
            //Utils.waitFor(1); // To avoid rate limiting 
        }
    }
}
