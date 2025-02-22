package es.uma.CoT;

import java.util.LinkedHashMap;
import java.util.Map;

public class CategoryPrompts {
    Map<String, String> list = new LinkedHashMap<>();

    public CategoryPrompts() {

        String baselinePrompt = 
            """
            # Category: Baseline Instances
            Create typical or standard instances that align with standard cases. Ensure every class and relationship is represented at least once in a baseline configuration.

            """;

        String boundaryPrompt = 
            """
            # Category: Boundary Instances
            Create instances that cover corner cases such as:
            - Minimum and maximum multiplicities.
            - Empty collections for optional associations.
            - Extreme values for numeric or range invariants constraints.

            """;

        String complexPrompt = 
            """
            # Category: Complex Instances
            Create complex instances that contain multiple interrelated entities and/or entities that are involved in multiple constraints.

            """;

        String unrealisticPrompt = 
            """
            # Category: Unrealistc but valid
            Create instances that are both syntactically and semantically valid but unlikely or impossible in real life. In terms of semantics, take into account constraints and multiplicities. Think about edge cases and uncommon combinations of relationships and attributes.

            """;
            
        String invalidPrompt = 
            """
            # Category: Realistic but invalid
            Create instances that make sense in real-life scenarios but violate the model constraints, exposing overly restrictive or unrealistic constraints.

            """;

        list.put("baseline", baselinePrompt);
        list.put("boundary", boundaryPrompt);
        list.put("complex", complexPrompt);
        list.put("unrealistic", unrealisticPrompt);
        list.put("invalid", invalidPrompt);

    }
}
