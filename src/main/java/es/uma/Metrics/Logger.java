package es.uma.Metrics;

import es.uma.Experiment;
import es.uma.Utils;

public class Logger {
    
    private static IMetrics getSystemMetrics(String system) {
        switch (system.toLowerCase()) {
            case "bank":
                return new Bank();
            // Add more cases here for other systems
            default:
                throw new IllegalArgumentException("Unknown system: " + system);
        }
    }
    
    public static void saveMetrics(Experiment experiment) {
        IMetrics sysMetrics = getSystemMetrics(experiment.system);
        IMetrics generalMetrics = new General();
        String diagram = experiment.umlPath;
        String type = experiment.type;

        for (int i = 1; i <= experiment.repetitions; i++) {
            String genPath = experiment.instancePath + "gen" + i + "/";
            
            Utils.saveFile("# Generation " + i + " \n", genPath, "metrics.md");

            String general = generalMetrics.getMetrics(diagram, genPath, type);
            Utils.saveFile(general, genPath, "metrics.md");
            
            //String system = sysMetrics.getMetrics(diagram, genPath, type);
            //Utils.saveFile(system, genPath, "metrics.md");
        }

        String generalSummary = generalMetrics.getSummary();
        //String systemSummary = sysMetrics.getSummary();
        Utils.saveFile(generalSummary, experiment.instancePath, "metrics.md");
        //Utils.saveFile(systemSummary, experiment.instancePath, "metrics.md");
    }
}
