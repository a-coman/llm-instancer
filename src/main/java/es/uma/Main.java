package es.uma;

import es.uma.Metrics.Logger;

public class Main {
    public static void main(String[] args) {
        Experiment experiment = new Experiment(Model.GEMINI_2_FLASH_LITE, "Simple", "bank", 2);
        experiment.run();
        ListenerMetrics.save(experiment.instancePath);
        Logger.saveMetrics(experiment);
    }
}
