package es.uma;

import es.uma.Metrics.MetricsRunner;

public class Main {
    public static void main(String[] args) {
        Experiment experiment = new Experiment(Model.GPT_4O, "CoT", "football", 30, Size.NONE);   
        experiment.run();
        Logger.save(experiment.instancePath);
        MetricsRunner metricsRunner = new MetricsRunner();
        metricsRunner.run(experiment);
    }
}
