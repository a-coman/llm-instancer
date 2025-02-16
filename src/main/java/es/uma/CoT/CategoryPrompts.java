package es.uma.CoT;

import java.util.LinkedHashMap;
import java.util.Map;

public class CategoryPrompts {
    Map<String, String> list = new LinkedHashMap<>();

    public CategoryPrompts() {

        String baselinePrompt = """
                # Category: Baseline Instances
                Describe typical or standard instances that align with common expectations of the model. Ensure every class and relationship is represented at least once in a baseline configuration.

                """;        
        String boundaryPrompt = """
                # Category: Boundary Instances
                Create instances that test boundary values of constraints, such as:
                - Minimum and maximum multiplicities.
                - Empty collections for optional associations.
                - Extreme values for numeric or range invariants constraints.

                """;
        String complexPrompt = """
                # Category: Complex Instances
                Describe complex instances involving multiple interrelated entities, testing interactions between constraints, relationships, and multiplicities.

                """;
        String unrealisticPrompt = """
                # Category: Unrealistc but valid
                Develop instances that are syntactically and constraints-wise/multiplicity-wise valid but unlikely or impossible in real life. Edge cases and uncommon combinations of relationships and attributes.

                """;

        String invalidPrompt = """
                # Category: Realistic but invalid
                Create instances that make sense in real-life scenarios but violate constraints exposing overly restrictive or unrealistic constraints.
                
                """;

        list.put("baseline", baselinePrompt);
        list.put("boundary", boundaryPrompt);
        list.put("complex", complexPrompt);
        list.put("unrealistic", unrealisticPrompt);
        list.put("invalid", invalidPrompt);

    }
}
