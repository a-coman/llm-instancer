package es.uma;

import dev.langchain4j.model.chat.ChatLanguageModel;
import es.uma.CoT.CategoryPrompts;
import es.uma.CoT.CoT;
import es.uma.Simple.Simple;

public class Experiment {
    
    private String umlPath;
    private String examplePath; // Syntax example
    private String instancePath;
    private ChatLanguageModel model;
    private int repetitions;
    private String system; // Name of the folder where the domain to be run is
    private String type; // CoT or Simple
    private String sizePrompt;
    private Model modelName;

    public Experiment(Model model, String type, String system, int repetitions, String time, Size size) {
        this.system = system;
        this.type = type;
        this.modelName = model;
        this.model = Llms.getModel(model);
        this.repetitions = calculateRepetitions(type, repetitions);
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

    public Model getModelName(){
        return modelName;
    }

    // For MetricsRunner Scalability
    public Experiment(String system, String instancePath) {
        this.umlPath = "./src/main/resources/prompts/" + system + "/diagram.use";
        this.instancePath = instancePath;
        this.system = system;
        this.type = "";
        this.model = null;
        this.repetitions = 1;
        this.sizePrompt = null;
        this.examplePath = null;
    }

    public Experiment(Model model, String type, String system, int repetitions, Size size) {
        this(model, type, system, repetitions, Utils.getTime(), size);
    }

    public Experiment(Model model, String type, String system, int repetitions, String time) {
        this(model, type, system, repetitions, time, Size.NONE);
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

    public ChatLanguageModel getModel() {
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
                // If repetitions is less than the number of categories, we generate at least one for each category. Otherwise, we round up to the nearest integer that fits for the number of categories.
                CategoryPrompts CATEGORY_PROMPTS = new CategoryPrompts("");
                int numberOfGenerations = repetitions < CATEGORY_PROMPTS.list.size() ? 1 : (int) Math.ceil((double) repetitions / CATEGORY_PROMPTS.list.size());
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
