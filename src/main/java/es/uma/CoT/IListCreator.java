package es.uma.CoT;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface IListCreator {
    String system = 
        """
        Your task is to generate a complete and diverse list of instances for a given category based on a provided conceptual model description. Each instance must:
        1. Be self-contained: Include all required attributes, relationships, and related entities in full detail.
        2. Conform to the model: Fulfil the constraints, multiplicities, relatinoships and attributes defined in the model expressed as a class diagram.
        3. Understand the context: Ensure that each instance and its attributes and relationships are relevant.
        4. Avoid duplication of instances: Take into consideration those instances previously built to avoid redundancy.
        5. Semantic diversity: From a semantic point of view, incorporate varied scenarios, including regional, linguistic, or cultural differences.
        6. Structural diversity: Include instances with different number of elements, different number of relationships and complexity, and create varied examples by changing entity attributes.
        """;

    String message = 
        """
        {{categoryPrompt}}
        {{modelDescription}}
        """;
        
    @SystemMessage(system)
    @UserMessage(message)
    String chat(@V("categoryPrompt") String categoryPrompt, @V("modelDescription") String modelDescription);
}
