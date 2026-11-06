package es.uma;

import dev.langchain4j.model.chat.ChatModel;
import es.uma.CoT.CategoryPrompts;
import es.uma.ProjectPaths.ModelSet;
import es.uma.CoT.CoT;
import es.uma.Simple.Simple;

public class Experiment {

    private String umlPath;
    private String examplePath; // Syntax example
    private String instancePath;
    private ChatModel model;
    private int repetitions;
    private String system; // Name of the folder where the domain to be run is
    private String type; // CoT or Simple
    private String sizePrompt;
    private Model modelName;

    public Experiment(Model model, String type, String system, int repetitions, String time, Size size,
            ModelSet modelSet) {
        this.system = system;
        this.type = type;
        this.modelName = model;
        this.model = Llms.getModel(model);
        this.repetitions = calculateRepetitions(type, repetitions);
        this.sizePrompt = size.getPrompt();
        umlPath = ProjectPaths.diagramPath(system, modelSet);
        examplePath = ProjectPaths.modelPath("syntax_example.soil");

        // Folder name: <time>[-<size>][-ocl]
        String folder = time;
        if (!size.equals(Size.NONE)) {
            folder += "-" + size;
        }
        if (modelSet == ModelSet.MODELS_WITH_OCL) {
            folder += "-ocl";
        }
        instancePath = ProjectPaths.instancePath(model, type, system, folder);

        Logger.setExperiment(this);
    }

    public Model getModelName() {
        return modelName;
    }

    public Experiment(Model model, String type, String system, int repetitions, Size size, ModelSet modelSet) {
        this(model, type, system, repetitions, Utils.getTime(), size, modelSet);
    }

    public Experiment(Model model, String type, String system, int repetitions, Size size) {
        this(model, type, system, repetitions, size, ModelSet.MODELS);
    }

    public Experiment(Model model, String type, String system, int repetitions, String time) {
        this(model, type, system, repetitions, time, Size.NONE, ModelSet.MODELS);
    }

    public String getUmlPath() {
        return umlPath;
    }

    public String getExamplePath() {
        return examplePath;
    }

    public String getInstancePath() {
        return instancePath;
    }

    public ChatModel getModel() {
        return model;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public String getSystem() {
        return system;
    }

    public String getType() {
        return type;
    }

    public String getSizePrompt() {
        return sizePrompt;
    }

    private int calculateRepetitions(String type, int repetitions) {
        switch (type) {
            case "CoT":
                // If repetitions is less than the number of categories, we generate at least
                // one for each category. Otherwise, we round up to the nearest integer that
                // fits for the number of categories.
                CategoryPrompts CATEGORY_PROMPTS = new CategoryPrompts("");
                int numberOfGenerations = repetitions < CATEGORY_PROMPTS.list.size() ? 1
                        : (int) Math.ceil((double) repetitions / CATEGORY_PROMPTS.list.size());
                return numberOfGenerations;
            default:
                return repetitions;
        }
    }

    public void run() {
        switch (type) {
            case "Simple":
                Simple.run(this);
                break;
            case "CoT":
                CoT.run(this);
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type + ". Valid types: Simple, CoT");
        }
    }
}
