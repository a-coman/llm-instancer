package es.uma.Simple;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ISimple {
    String system = """
            Given a conceptual model in the UML-based Specification Environment and a syntax example for instance creation, your task is to generate valid instances that conform to the provided model.
            ### Requirements
            - Instances must be correctly formatted according to the syntax example.
            - Avoid unnecessary commentary and output the instance in plain text (not markdown).
            - Ensure strict adherence to the model's constraints, multiplicities, relatinoships and attributes.
            - Provide multiple instances with diverse data values and structures.
            - Additionally provide instances that cover edge cases for constraints, multiplicities, and attribute values.
            """;

    String message = """
            Create instances for the following UML model:
            {{modelUML}}

            Here is an example of the language syntax for creating instances:
            {{syntaxExample}} 
            """;
            
    @SystemMessage(system)
    @UserMessage(message)
    String chat(@V("modelUML") String modelUML, @V("syntaxExample") String syntaxExample);
}