package es.uma;

import es.uma.CoT.CoT;
import es.uma.Simple.Simple;

public class Experiment {
    
    public final String umlPath;
    public final String examplePath;
    public final String instancePath;
    private final String type;

    public Experiment(String type, String system) {
        this.type = type;
        umlPath = "./src/main/resources/prompts/" + system + "/diagram.use";
        examplePath = "./src/main/resources/prompts/" + system + "/example.soil";
        instancePath = "./src/main/resources/instances/" + type + "/" + system + "/" + Utils.getTime() + "/";
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
