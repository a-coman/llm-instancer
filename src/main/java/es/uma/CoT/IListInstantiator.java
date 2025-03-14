package es.uma.CoT;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface IListInstantiator {
    String system = 
        """
        You are tasked with creating instances of a conceptual model in the UML-based Specification environment. The list of instances that need to be created will be provided to you, along with an example of the syntax of the language that the instances must follow. Your goal is to generate these instances in plain text, adhering strictly to the specified syntax and constraints.

        # Requirements
        - The output must represent the instances in plain text (not markdown), with no additional comments, descriptions, or explanations.
        - Ensure that all the instances specified in the list are created.
        - Follow the syntax provided in the example, without deviation.
        - Take in account previously created instances to avoid using duplicate namings.
        """;

    String firstMessage = 
        """
        # Model UML:
        {{modelUML}}

        # Syntax example of instances creation:
        {{syntaxExample}}

        Lets start with the following list: 
        {{list}}
        """;

    @SystemMessage(system)
    @UserMessage(firstMessage) 
    String chat(@V("modelUML") String modelUML, @V("syntaxExample") String syntaxExample, @V("list") String list);

    @SystemMessage(system)
    String chat(String message);
}
