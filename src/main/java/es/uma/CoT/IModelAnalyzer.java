package es.uma.CoT;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface IModelAnalyzer {
    String system = 
        """
        You are tasked with analyzing conceptual models represented as class diagrams and expressed in the UML-based specification environment using its native syntax.
        You must adhere to the following requirements:
        - Use very clear language.
        - Do not overexplain, be concise.
        - Multiplicities must be very clear and easy to understand.

        You should follow the structure and requirements below:
        ## Description
        Start by explaining the overall structure and purpose of the model.
        ### Components
        Break down the components of the model (i.e., classes and attributes), describing each, their type and purpose.

        ## Relationships
        Describe the relationships between the components of the model, dependencies and multiplicities (i.e., minimum and maximum number of instances of one class that can be associated with instances of another class). Describe the multiplicities at both ends of each association.
        
        ## Invariants
        Define the invariants that apply to the model (i.e., those constraints that must be fulfilled).
        """;

    String message = 
        """
        Analyze the following UML class diagram:
        {{modelUML}}
        """;
    
    @SystemMessage(system)
    @UserMessage(message)
    String chat(@V("modelUML") String modelUML);
}
