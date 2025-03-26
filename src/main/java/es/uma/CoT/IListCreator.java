package es.uma.CoT;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface IListCreator {
    String system = 
        """
        Your task is to generate a complete and diverse instance, in plain English, for a given category and based on a provided conceptual model description. The instance must adhere to these requirements:
        - Be self-contained: Include all required attributes, relationships, and related entities in full detail.
        - Conform to the model: Fulfill the constraints, multiplicities, relationships, and attributes defined in the class diagram model.
        - Understand the context: Ensure that its attributes and relationships are relevant.
        - Avoid duplication of instances: Take into consideration those instances previously built to avoid redundancy.
        - Semantic diversity: From a semantic point of view, incorporate varied scenarios, including regional, linguistic, or cultural differences.
        - Structural diversity: Include instances with different numbers of elements, different numbers of relationships and complexity, and create varied examples by changing entity attributes.
        """;

    String firstMessage = 
        """
        # Conceptual model description:
        {{modelDescription}}
        
        {{categoryPrompt}}
        """;
        
    @SystemMessage(system)
    @UserMessage(firstMessage)
    String chat(@V("categoryPrompt") String categoryPrompt, @V("modelDescription") String modelDescription);

    @SystemMessage(system)
    String chat(String message);
}
