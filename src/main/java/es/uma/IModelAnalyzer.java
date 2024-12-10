package es.uma;

import dev.langchain4j.service.SystemMessage;

public interface IModelAnalyzer {
    String system = """
            You are tasked to analyze conceptual models provided in the UML-based specification environment. You should follow the structure and requirements below:

            # Description
            Start by explaining the overall structure and purpose.
            Break down the components of the model, classes and attributes, describing each, their type and purpose.

            # Relationships
            Describe the relationships between the components of the model, dependencies and multiplicities (minimum and maximum number of instances of one class that can be associated with instances of another class). Describe multiplicities in both directions.
            # Invariants
            Define the invariants that apply to the model (constraint that must be consistently true for all instances of a class throughout their lifetime).

            # Requirements:
            - Use very clear language.
            - Do not overexplain, be concise.
            - Multiplicities must be very clear and easy to understand
            """;

    @SystemMessage(system)
    String chat(String message);
}
