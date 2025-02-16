package es.uma.Simple;

import es.uma.Llms;
import es.uma.Utils;

public class Main {
    public static void main(String[] args) {
        ISimple simple = Llms.getAgent(ISimple.class, Llms.getModel("4o"));

        // Read model and example
        final String DIAGRAM = "bank";
        final String PROMPT_PATH = "./src/main/resources/prompts/" + DIAGRAM + "/";
        String modelUML = Utils.readFile(PROMPT_PATH + "diagram.use"); 
        String exampleSOIL = Utils.readFile(PROMPT_PATH + "examples/example_1.soil");

        String response = simple.chat(modelUML, exampleSOIL);

        System.out.println(response);
    }
}
