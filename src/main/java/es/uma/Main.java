package es.uma;

import es.uma.ProjectPaths.ModelSet;

public class Main {
    public static void main(String[] args) {
        RawConsoleLogs.run(context -> {
            Experiment experiment = null;

            // ModelSet.MODELS: resources/models; ModelSet.MODELS_WITH_OCL: resources/models-with-ocl
            experiment = new Experiment(Model.GPT_4O, "CoT", "videoclub", 30, Size.NONE, ModelSet.MODELS);
            context.setOutputPath(experiment.getInstancePath());
            experiment.run();
            Logger.save(experiment.getInstancePath());
        });
    }
}
