package es.uma.Simple;

import es.uma.Experiment;
import es.uma.Llms;
import es.uma.Utils;

public class Simple {
    public static void run(Experiment experiment) {
        ISimple simple = Llms.getAgent(ISimple.class, Llms.getModel(experiment.model));

        String modelUML = Utils.readFile(experiment.umlPath); 
        String exampleSOIL = Utils.readFile(experiment.examplePath);

        String response = simple.chat(modelUML, exampleSOIL);
        Utils.saveFile(response, experiment.instancePath, "instance1.soil");
        Utils.saveFile(response, experiment.instancePath, "output.md");

        for (int i = 2; i <= 5; i++) {
            response = simple.chat("Let's continue creating more instances");
            Utils.saveFile(response, experiment.instancePath, "instance" + i + ".soil");
            Utils.saveFile(response, experiment.instancePath, "output.md");
        }
    }
}
