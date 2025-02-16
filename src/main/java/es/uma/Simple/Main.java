package es.uma.Simple;

import es.uma.Agents;
import es.uma.Models;
import es.uma.Utils;

public class Main {
    public static void main(String[] args) {
        ISimple simple = Agents.getSimple(Models.getModel("4o"));
        
        // Read model and example
        final String DIAGRAM = "hammers";
        final String PROMPT_PATH = "./src/main/resources/prompts/" + DIAGRAM + "/";
        String modelUML = Utils.readFile(PROMPT_PATH + "diagram.use"); 
        String exampleSOIL = Utils.readFile(PROMPT_PATH + "examples/example_1.soil");

        String response = simple.chat(modelUML, exampleSOIL);

        System.out.println(response);
    }
}
