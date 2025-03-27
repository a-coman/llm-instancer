package es.uma.Simple;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ISimple {
    String system = 
        """
        Given a conceptual model expressed in the UML-based Specification Environment (USE) and a syntax example for instance creation, your task is to generate valid instances that conform to the provided model.

        ### Requirements
        - Instances must be syntactically correct according to the USE syntax example.
        - Avoid unnecessary comments and output the instance in plain text (i.e., not markdown).
        - Make sure instances fulfill all the model's constraints, and that multiplicities, relationships, and attributes are valid.
        - Provide multiple instances with diverse data values and structure.
        - Additionally, provide instances that cover corner cases for constraints, multiplicities, and attribute values.
        """;

    String message = 
        """
        Create an instance{{size}} for the following UML class diagram:
        {{modelUML}}

        Here is an example of the language syntax for creating instances:
        {{syntaxExample}} 
        """;
        
    @SystemMessage(system)
    @UserMessage(message)
    String chat(@V("modelUML") String modelUML, @V("syntaxExample") String syntaxExample, @V("size") String sizePrompt);

    @SystemMessage(system)
    String chat(String message);
}