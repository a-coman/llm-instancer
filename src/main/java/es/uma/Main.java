package es.uma;

import es.uma.Metrics.MetricsRunner;

public class Main {
    public static void main(String[] args) {
        RawConsoleLogs.run(context -> {
            Experiment experiment = null;

            experiment = new Experiment(Model.FREE, "CoT", "bank", 1, Size.NONE);
            context.setOutputPath(experiment.getInstancePath());
            experiment.run();
            Logger.save(experiment.getInstancePath());
            new MetricsRunner().run(experiment);
        });
    }
}
