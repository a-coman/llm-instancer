package es.uma;

import dev.langchain4j.model.chat.ChatLanguageModel;
import es.uma.CoT.CoT;
import es.uma.Simple.Simple;

public class Experiment {
    
    public final String umlPath;
    public final String examplePath;
    public final String instancePath;
    public final ChatLanguageModel model;
    public final int repetitions;
    private final String type;

    public Experiment(Model model, String type, String system, int repetitions) {
        this.type = type;
        this.model = Llms.getModel(model);
        this.repetitions = repetitions;
        umlPath = "./src/main/resources/prompts/" + system + "/diagram.use";
        examplePath = "./src/main/resources/prompts/" + system + "/example.soil";
        instancePath = "./src/main/resources/instances/" + type + "/" + system + "/" + model.toString() + "/" + Utils.getTime() + "/";
        Listener.logsPath = instancePath;
        Metrics.repetitions = repetitions;
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
        Metrics.save(instancePath);
    }
}
