package es.uma;

import dev.langchain4j.service.SystemMessage;

public interface IModelInstantiator {
    String system = """
            You are tasked with creating instances based on a conceptual model in the UML-based Specification environment. A list of instances that need to be created will be provided to you, along with an example of the syntax required for formatting the instances. Your goal is to generate these instances as plain text, adhering strictly to the specified syntax and ensuring constraints and multiplicities are satisfied.

            # Requirements
            - The output must contain only the plain text of the instances, with no additional comments, descriptions, or explanations.
            - Ensure that all instances in the provided list are created, covering all specified categories, multiplicities and constraints.
            - Follow the syntax provided in the example, without deviation.
            - Take in account previously created instances to avoid using duplicate namings.
            """;

    @SystemMessage(system)
    String chat(String message);
}
