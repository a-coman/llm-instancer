package es.uma.CoT;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface IListInstantiator {
    String system = 
        """
        You are tasked with creating instances of a conceptual model in the UML-based Specification Environment (USE). You will receive:
        1. The UML class diagram that the instance follows.
        2. A sample syntax of instances creation. 
        3. A description of the instance that needs to be created.

        Your goal is to generate these instances based on the provided description, adhering strictly to these requirements:
        - The output must be in plain text, with no additional comments, descriptions, or explanations.
        - Ensure that the created instance adheres to the provided description.
        - Follow the syntax sample provided, without deviation.
        - Take into account previously created instances to avoid using duplicate naming.
        """;

    String firstMessage = 
        """
        # UML class diagram:
        {{modelUML}}

        # Syntax example of instances creation:
        {{syntaxExample}}

        # Instance description:
        Please create the instance according to this specification:
        {{list}}
        """;

    @SystemMessage(system)
    @UserMessage(firstMessage) 
    String chat(@V("modelUML") String modelUML, @V("syntaxExample") String syntaxExample, @V("list") String list);

    @SystemMessage(system)
    String chat(String message);
}
