package es.uma;

import es.uma.Metrics.MetricsRunner;

public class Main {
    public static void main(String[] args) {
        Experiment experiment = new Experiment(Model.GEMINI_2_FLASH_LITE, "CoT", "bank", 2);
        experiment.run();
        ListenerMetrics.save(experiment.instancePath);
        MetricsRunner metricsRunner = new MetricsRunner();
        metricsRunner.run(experiment);
    }
}
