package es.uma.Metrics;

import es.uma.Experiment;
import es.uma.Utils;
import es.uma.CoT.CategoryPrompts;

public class MetricsRunner {
    public void run(Experiment experiment) {
        StringBuilder sb = new StringBuilder();
        General sumGeneral = new General();
        IMetrics sumSpecific = MetricsFactory.createMetrics(experiment.system);

        // For each generation
        for (int gen = 1; gen <= experiment.repetitions; gen++) {
            sb.append("# Generation ").append(gen).append("\n");
            General genGeneral = new General();
            IMetrics genSpecific = MetricsFactory.createMetrics(experiment.system);

            if (experiment.type.equals("CoT")) {
                CategoryPrompts categoryPrompts = new CategoryPrompts();
                for (String category : categoryPrompts.list.keySet()) {
                    String diagramPath = experiment.umlPath;
                    String instancePath = experiment.instancePath + "gen" + gen + "/" + category + ".soil";
                    
                    sb.append("## Category ").append(category).append("\n");
                    String instance = Utils.readFile(instancePath);
                    sb.append("```\n").append(instance).append("\n```\n");

                    // Calculate category-level metrics
                    General catGeneral = new General();
                    IMetrics catSpecific = MetricsFactory.createMetrics(experiment.system);

                    // For invalid instances -> partial metrics
                    if(category.equals("invalid")) {
                        catGeneral.calculateInvalid(diagramPath, instancePath);
                        catSpecific.calculateInvalid(diagramPath, instancePath);
                    } else {
                        catGeneral.calculate(diagramPath, instancePath);
                        catSpecific.calculate(diagramPath, instancePath);
                    }

                    // Aggregate category metrics into generation metrics
                    genGeneral.aggregate(catGeneral);
                    genSpecific.aggregate(catSpecific);
                    
                    // Output category metrics
                    sb.append(catGeneral.toString()).append("\n");
                    sb.append(catSpecific.toString()).append("\n");
                    
                }
            }

            if (experiment.type.equals("Simple")) {
                String diagramPath = experiment.umlPath;
                String instancePath = experiment.instancePath + "gen" + gen + "/output.soil";
                genGeneral.calculate(diagramPath, instancePath);
                genSpecific.calculate(diagramPath, instancePath);
                String instance = Utils.readFile(instancePath);
                sb.append("```\n").append(instance).append("\n```\n");
            }

            // Aggregate generation metrics into summary metrics
            sumGeneral.aggregate(genGeneral);
            sumSpecific.aggregate(genSpecific);

            // Output generation metrics
            sb.append("## Generation ").append(gen).append(" summary\n");
            sb.append(genGeneral.toString()).append("\n");
            sb.append(genSpecific.toString()).append("\n");
        }

        // Output summary metrics and save
        sb.append("# Summary for all generations\n");
        sb.append(sumGeneral.toString()).append("\n");
        sb.append(sumSpecific.toString()).append("\n");

        Utils.saveFile(sb.toString(), experiment.instancePath, "metrics.md");
    }
}
