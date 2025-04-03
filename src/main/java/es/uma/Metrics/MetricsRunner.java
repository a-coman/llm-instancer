package es.uma.Metrics;

import java.util.ArrayList;
import java.util.Arrays;

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
                CategoryPrompts categoryPrompts = new CategoryPrompts(experiment.sizePrompt);
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

            if (experiment.type.isEmpty()) {
                String diagramPath = experiment.umlPath;
                String instancePath = experiment.instancePath;
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

            if (experiment.type.equals("CoT")) {
                sb.append(genGeneral.invalidToString()).append("\n");    
            }
            
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
        
        if (experiment.type.equals("CoT")) {
            sb.append(sumGeneral.invalidToString()).append("\n");
        }

        sb.append(sumSpecific.toString()).append("\n");

        Utils.saveFile(sb.toString(), experiment.instancePath, "metrics.md", false);
    }

    // Run metrics for specific experiment
    public static void main(String[] args) {
        // ArrayList<String> systems = new ArrayList<>(Arrays.asList("addressbook", "bank", "hotelmanagement", "myexpenses", "pickupnet", "statemachine", "vehiclerental", "videoclub"));

        // // Simple
        // ArrayList<String> simpleTimes = new ArrayList<>(Arrays.asList("21-03-2025--17-36-43", "21-03-2025--15-41-00", "22-03-2025--18-31-34", "22-03-2025--17-12-34", "21-03-2025--15-53-43", "22-03-2025--18-08-16", "21-03-2025--16-07-51", "21-03-2025--16-37-44"));
        
        // for (int i = 0; i < systems.size(); i++) {
        //     Experiment experiment = new Experiment(Model.GPT_4O, "Simple", systems.get(i), 30, simpleTimes.get(i));
        //     MetricsRunner metricsRunner = new MetricsRunner();
        //     metricsRunner.run(experiment);  
        // }

        // // CoT
        // ArrayList<String> cotTimes = new ArrayList<>(Arrays.asList("25-03-2025--10-38-22", "24-03-2025--21-13-02", "25-03-2025--11-38-06", "25-03-2025--13-12-54", "25-03-2025--13-32-07", "25-03-2025--13-49-01", "25-03-2025--14-02-01", "25-03-2025--14-18-14"));

        // for (int i = 0; i < systems.size(); i++) {
        //     Experiment experiment = new Experiment(Model.GPT_4O, "CoT", systems.get(i), 30, cotTimes.get(i));
        //     MetricsRunner metricsRunner = new MetricsRunner();
        //     metricsRunner.run(experiment);  
        // }

        // ArrayList<String> systems = new ArrayList<>(Arrays.asList("addressbook", "bank", "hotelmanagement", "myexpenses", "pickupnet", "vehiclerental", "videoclub"));
        // ArrayList<String> scalability = new ArrayList<>(Arrays.asList("15","30","50","100"));

        // for (String system : systems) {
        //     for (String scale : scalability) {
        //         Experiment experiment = new Experiment(system, "src/main/resources/instances/Scalability/" + system + "/" + system + scale + ".soil");
        //         MetricsRunner metricsRunner = new MetricsRunner();
        //         metricsRunner.run(experiment);  
        //     }
            
        // }

        Experiment experiment = new Experiment(Model.GPT_4O, "Simple", "bank", 30, "21-03-2025--15-41-00");
        MetricsRunner metricsRunner = new MetricsRunner();
        metricsRunner.run(experiment);
    }
}
