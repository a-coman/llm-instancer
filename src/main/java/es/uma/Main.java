package es.uma;

import es.uma.Metrics.MetricsRunner;

public class Main {
    public static void main(String[] args) {
        Experiment experiment = new Experiment(Model.GEMINI_2_FLASH_LITE, "CoT", "bank", 1);
        experiment.run();
        Logger.save(experiment.instancePath);
        MetricsRunner metricsRunner = new MetricsRunner();
        metricsRunner.run(experiment);
    }
}
