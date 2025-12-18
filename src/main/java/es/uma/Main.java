package es.uma;

import es.uma.Metrics.MetricsRunner;

public class Main {
    public static void main(String[] args) {
        Experiment experiment = new Experiment(Model.GPT_5_2, "Simple", "restaurant", 30, Size.NONE);   
        experiment.run();
        Logger.save(experiment.getInstancePath());
        MetricsRunner metricsRunner = new MetricsRunner();
        metricsRunner.run(experiment);
    }
}
