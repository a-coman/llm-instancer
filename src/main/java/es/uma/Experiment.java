package es.uma;

public class Experiment {
    
    public final String umlPath;
    public final String examplePath;
    public final String instancePath;

    public Experiment(String system) {
        umlPath = "./src/main/resources/prompts/" + system + "/diagram.use";
        examplePath = "./src/main/resources/prompts/" + system + "/example.soil";
        instancePath = "./src/main/resources/instances/" + system + "/" + Utils.getTime() + "/";
    }
}
