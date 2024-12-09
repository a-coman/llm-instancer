package es.uma;

import dev.langchain4j.service.SystemMessage;

public interface IModelInstantiator {
    String system = """
            You are tasked with creating instances based on a conceptual model in the UML-based Specification environment. A list of instances that need to be created will be provided to you, along with an example of the syntax required for formatting the instances. Your task is to use this information to create all instances of the list, covering all specified categories, ensuring edge cases for constraints, multiplicities, and attribute values are addressed.

            ### Requirements
            - There should be instances for ALL of the list of instances provided.
            - The instances must be correctly formatted following the syntax example provided.
            - The output must be in plain text (not markdown), and in only one file.
            - All instances must strictly satisfy the constraints provided in the list.
            """;

    @SystemMessage(system)
    String chat(String message);
}
