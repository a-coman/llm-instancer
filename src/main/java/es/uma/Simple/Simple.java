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

        System.out.println(response);
    }
}
