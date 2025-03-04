package es.uma.Simple;

// import java.util.concurrent.TimeUnit;

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

        String response = simple.chat(modelUML, exampleSOIL);
        Utils.saveFile(Utils.removeComments(response), experiment.instancePath, "gen1.soil");
        Utils.saveFile(Utils.removeComments(response) + "\n", experiment.instancePath, "output.soil");
        use.checkSyntax(experiment.umlPath, experiment.instancePath + "gen1.soil");
        use.checkRestrictions(experiment.umlPath, experiment.instancePath + "gen1.soil", "");

        for (int i = 2; i <= experiment.repetitions; i++) {
            response = simple.chat("Let's continue creating more instances");
            Utils.saveFile(Utils.removeComments(response), experiment.instancePath, "gen" + i + ".soil");
            Utils.saveFile(Utils.removeComments(response) + "\n", experiment.instancePath, "output.soil");
            use.checkSyntax(experiment.umlPath, experiment.instancePath + "gen" + i + ".soil");
            use.checkRestrictions(experiment.umlPath, experiment.instancePath + "gen" + i + ".soil", "");

            // Wait for 1 minute before generating the next instance to avoid rate limiting
            // try {
            //     System.out.println("\n\nWaiting for 1 minute before generating the next instance...\n");
            //     TimeUnit.MINUTES.sleep(1);
            // } catch (Exception e) {
            //     throw new RuntimeException(e);
            // }
        }
    }
}
