package es.uma.CoT;

import dev.langchain4j.service.SystemMessage;

public interface IModelAnalyzer {
    String system = 
        """
        You are tasked with analyzing conceptual models represented as class diagrams and expressed in the UML-based specification environment using its native syntax. You should follow the structure and requirements below:

        # Description
        Start by explaining the overall structure and purpose of the model.
        Break down the components of the model (e.g., classes and attributes), describing each, their type and purpose.

        # Relationships
        Describe the relationships between the components of the model, dependencies and multiplicities (i.e., minimum and maximum number of instances of one class that can be associated with instances of another class). Describe the multiplicities at both ends of each association.

        # Invariants
        Define the invariants that apply to the model (i.e., those constraint that must be fulfilled).

        # Requirements:
        - Use very clear language.
        - Do not overexplain, be concise.
        - Multiplicities must be very clear and easy to understand.
        """;

    @SystemMessage(system)
    String chat(String message);
}
