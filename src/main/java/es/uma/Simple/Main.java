package es.uma.Simple;

import es.uma.Experiment;
import es.uma.Llms;
import es.uma.Utils;

public class Main {
    public static void main(String[] args) {
        Experiment experiment = new Experiment("bank");
        ISimple simple = Llms.getAgent(ISimple.class, Llms.getModel("4o"));

        String modelUML = Utils.readFile(experiment.umlPath); 
        String exampleSOIL = Utils.readFile(experiment.examplePath);

        String response = simple.chat(modelUML, exampleSOIL);

        System.out.println(response);
    }
}
