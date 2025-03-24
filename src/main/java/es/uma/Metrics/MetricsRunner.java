package es.uma.Metrics;

import es.uma.Experiment;
import es.uma.Model;
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

                    // Calculate and output category metrics
                    switch (category) {
                        // For invalid instances -> partial checks (syntax), all semantics
                        case "invalid":
                            catGeneral.calculateInvalid(diagramPath, instancePath);
                            catSpecific.calculate(diagramPath, instancePath);        
                            sb.append(catGeneral.invalidToString()).append("\n");
                            sb.append(catSpecific.toString()).append("\n");
                            break;
                        // Otherwise -> all checks, all semantics
                        default:
                            catGeneral.calculate(diagramPath, instancePath);
                            catSpecific.calculate(diagramPath, instancePath);
                            sb.append(catGeneral.toString()).append("\n");
                            sb.append(catSpecific.toString()).append("\n");
                            break;
                    }

                    // Aggregate category metrics into generation metrics
                    genGeneral.aggregate(catGeneral);
                    genSpecific.aggregate(catSpecific);
                    
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
        sb.append("| Summary | Value | \n").append("|---|---| \n");
        sb.append("| Model | ").append(experiment.modelName).append(" | \n");
        sb.append("| Type | ").append(experiment.type).append(" | \n");
        sb.append("| System | ").append(experiment.system).append(" | \n");
        sb.append("| Number of generations | ").append(experiment.repetitions).append(" | \n\n");
        sb.append(sumGeneral.toString()).append("\n");
        sb.append(sumSpecific.toString()).append("\n");

        Utils.saveFile(sb.toString(), experiment.instancePath, "metrics.md", false);
    }

    // Run metrics for specific experiment
    public static void main(String[] args) {
        Experiment experiment = new Experiment(Model.GEMINI_2_FLASH_LITE, "Simple", "myexpenses", 1, "22-03-2025--16-30-26");
        MetricsRunner metricsRunner = new MetricsRunner();
        metricsRunner.run(experiment);
    }
}
