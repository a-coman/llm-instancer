package es.uma.CoT;

import java.util.LinkedHashMap;
import java.util.Map;

public class CategoryPrompts {
    public Map<String, String> list = new LinkedHashMap<>();

    public CategoryPrompts(String sizePrompt) {

        String baselinePrompt = 
            """
            # Category: Baseline Instances
            Create a baseline instance{{size}}. This is an instance that represents a typical/standard scenario. Ensure every class and relationship is present in the instance at least once.
            """;

        String boundaryPrompt = 
            """
            # Category: Boundary Instances
            Create a boundary case instance{{size}}. This is an instance that focuses on the extreme upper or lower limits of valid input ranges. For example:
            - Upper or lower limits of multiplicities.
            - For numbers in a range, the minimum and maximum valid values.
            - Empty collections when possible, i.e., when they do not violate the semantics of the model or its constraints.
            """;

        String complexPrompt = 
            """
            # Category: Complex Instances
            Create a complex instance{{size}} that contains multiple interrelated entities and/or entities that are involved in multiple constraints.
            """;

        String edgePrompt = 
            """
            # Category: Edge Instances
            Create an edge case instance{{size}}. This is an instance that behaves within but at the limit of the expected behavior. This instance must focus on a scenario that is unusual or unlikely in real life but possible according to the syntax and semantics of the model. In terms of semantics, take into account constraints, multiplicities, and uncommon combinations of relationships and attributes.
            """;
            
        String invalidPrompt = 
            """
            # Category: Overconstraint Detection
            Create an instance{{size}} that represents a real-life scenario that is logically valid but violates the model's multiplicities or constraints, exposing overly restrictive or unrealistic restrictions.
            """;

        list.put("baseline", baselinePrompt);
        list.put("boundary", boundaryPrompt);
        list.put("complex", complexPrompt);
        list.put("edge", edgePrompt);
        list.put("invalid", invalidPrompt);

        list.forEach((key, value) -> list.put(key, value.replace("{{size}}", sizePrompt)));
    }
}
