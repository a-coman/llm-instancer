package es.uma.CoT;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface IListCreator {
    String system = """
            Your task is to generate a complete and diverse set of instances for a given category based on a provided conceptual model description. Each instance must:
            1. Be self-contained: Include all required attributes, relationships, and related entities in full detail.
            2. Follow the model: Adhere to the constraints, multiplicities, and relationships defined in the UML specification.
            3. Understand the context: Ensure that the instances and their attributes are relevant to the overall model.
            4. Avoid duplication: Build on prior instances without redundancy, clearly describing any interconnections, and no duplicate namings.
            5. Regional diversity: Incorporate varied scenarios, including regional, linguistic, or cultural differences.
            6. Structural diversity: Include instances with different shapes and complexity, and create varied examples by changing entity attributes.
            """;

    String message = """
            # Category:
            {{categoryDescription}}
            # Model description:
            {{modelDescription}}
            """;
            
    @SystemMessage(system)
    @UserMessage(message)
    String chat(@V("categoryDescription") String categoryDescription, @V("modelDescription") String modelDescription);
}
