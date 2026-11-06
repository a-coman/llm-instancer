package es.uma;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Resolves where domain models are read from and where instances are written. Everything lives in
 * src/main/resources, using the same layout as instagen's docs folder:
 *   models/<system>/diagram.use            (docs/models/<system>/diagram.use)
 *   models-with-ocl/<system>/diagram.use   (docs/experts-judge/models-with-ocl/<system>/diagram.use)
 *   examples/<name>/diagram.use            (local extra models, gitignored)
 *   instances/<MODEL>/<Type>/<system>/<time>/gen<N>/...   (docs/instances/<MODEL>/...)
 */
public final class ProjectPaths {

    public enum ModelSet {
        MODELS("models"),
        MODELS_WITH_OCL("models-with-ocl");

        private final String dir;

        ModelSet(String dir) {
            this.dir = dir;
        }
    }

    private static final Path RESOURCES = Paths.get("src", "main", "resources");
    private static final Path EXAMPLES = RESOURCES.resolve("examples");
    private static final Path INSTANCES = RESOURCES.resolve("instances");

    private ProjectPaths() {
    }

    /** A file under the given model set, e.g. "bank/diagram.use" or "syntax_example.soil", falling back to examples/. */
    public static String modelPath(ModelSet set, String relative) {
        Path model = RESOURCES.resolve(set.dir).resolve(relative);
        if (Files.exists(model)) {
            return model.toString();
        }
        Path example = EXAMPLES.resolve(relative);
        if (Files.exists(example)) {
            return example.toString();
        }
        return model.toString(); // Missing everywhere: let the caller fail on the expected path
    }

    public static String modelPath(String relative) {
        return modelPath(ModelSet.MODELS, relative);
    }

    public static String diagramPath(String system, ModelSet set) {
        return modelPath(set, system.toLowerCase() + "/diagram.use");
    }

    public static String diagramPath(String system) {
        return diagramPath(system, ModelSet.MODELS);
    }

    /** Experiment folder: instances/<MODEL>/<Type>/<system>/<time>/ (with trailing slash). */
    public static String instancePath(Model model, String type, String system, String time) {
        return INSTANCES.resolve(model.toString()).resolve(type).resolve(system).resolve(time) + "/";
    }
}
