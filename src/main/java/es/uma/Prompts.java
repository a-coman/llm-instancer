package es.uma;

import java.util.HashMap;
import java.util.Map;

public class Prompts {
    Map<String, String> list = new HashMap<>();

    public Prompts() {

        String baseline = """
                # Category: Baseline Instances
                Describe typical or standard instances that align with common expectations of the model. Ensure every class and relationship is represented at least once in a baseline configuration.
                """;        
        String boundary = """
                # Category: Boundary Instances
                Create instances that test boundary values of constraints, such as:
                - Minimum and maximum multiplicities.
                - Empty collections for optional associations.
                - Extreme values for numeric or range invariants constraints.
                """;
        String complex = """
                # Category: Complex Instances
                Describe complex instances involving multiple interrelated entities, testing interactions between constraints, relationships, and multiplicities.
                """;
        String valid = """
                # Category: Unrealistc but valid
                Develop instances that are syntactically and constraints-wise/multiplicity-wise valid but unlikely or impossible in real life. Edge cases and uncommon combinations of relationships and attributes.
                """;

        String invalid = """
                # Category: Realistic but invalid
                Create instances that make sense in real-life scenarios but violate constraints exposing overly restrictive or unrealistic constraints.
                """;

        list.put("baseline", baseline);
        list.put("boundary", boundary);
        list.put("complex", complex);
        list.put("valid", valid);
        list.put("invalid", invalid);

    }
}
