package es.uma;

import dev.langchain4j.model.chat.ChatLanguageModel;
import es.uma.CoT.CategoryPrompts;
import es.uma.CoT.CoT;
import es.uma.Simple.Simple;

public class Experiment {
    
    public final String umlPath;
    public final String examplePath;
    public final String instancePath;
    public final ChatLanguageModel model;
    public final int repetitions;
    public final String system;
    public final String type;
    public final Model modelName;
    public final String sizePrompt;

    public Experiment(Model model, String type, String system, int repetitions, String time, Size size) {
        this.system = system;
        this.type = type;
        this.model = Llms.getModel(model);
        this.modelName = model;
        this.repetitions = getRepetitions(type, repetitions);
        this.sizePrompt = size.getPrompt();
        umlPath = "./src/main/resources/prompts/" + system + "/diagram.use";
        examplePath = "./src/main/resources/prompts/example.soil";
        
        if (size.equals(Size.NONE)) {
            instancePath = "./src/main/resources/instances/" + type + "/" + system + "/" + model.toString() + "/" + time + "/";    
        } else {
            instancePath = "./src/main/resources/instances/" + type + "/" + system + "/" + model.toString() + "/" + time + "-" + size + "/";
        }
        
        Logger.setExperiment(this);
    }

    private int getRepetitions(String type, int repetitions) {
        switch (type) {
            case "CoT":
                // If repetitions is less than the number of categories, we generate at least one for each category. Otherwise, we round up to the nearest integer that fits for the number of categories.
                CategoryPrompts CATEGORY_PROMPTS = new CategoryPrompts("");
                int numberOfGenerations = repetitions < CATEGORY_PROMPTS.list.size() ? 1 : (int) Math.ceil((double) repetitions / CATEGORY_PROMPTS.list.size());
                return numberOfGenerations;
            default:
                return repetitions;
        }
    }

    public Experiment(Model model, String type, String system, int repetitions, Size size) {
        this(model, type, system, repetitions, Utils.getTime(), size);
    }

    public Experiment(Model model, String type, String system, int repetitions, String time) {
        this(model, type, system, repetitions, time, Size.NONE);
    }

    // For MetricsRunner Scalability
    public Experiment(String system, String instancePath) {
        this.umlPath = "./src/main/resources/prompts/" + system + "/diagram.use";
        this.instancePath = instancePath;
        this.system = system;
        this.type = "";
        this.model = null;
        this.modelName = null;
        this.repetitions = 1;
        this.sizePrompt = null;
        this.examplePath = null;
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
                throw new IllegalArgumentException("Invalid type: " + type);
        }
    }
}
