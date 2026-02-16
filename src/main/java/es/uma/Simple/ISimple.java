package es.uma.Simple;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ISimple {
    String system = """
            Given a conceptual model expressed in the UML-based Specification Environment (USE), your task is to generate valid and realistic instances that conform to the provided model.
            <requirements>
            - Instances must be syntactically correct according to the USE syntax_reference.
            - Avoid unnecessary comments and output the instance in plain text (i.e., not markdown).
            - Make sure instances fulfill all the model's constraints, and that multiplicities, relationships, and attributes are valid and realistic.
            - Provide multiple instances with diverse data values and structure.
            </requirements>

            <syntax_reference>
            Here there is a snippet showing how to create objects and set values in the specific .soil language required:
            -- This is a comment example

            -- Primitive data types:
                -- Integer i.e. 1, 2, 3, etc.
                -- Real i.e. 1.0, 21.89, 322.05556, etc.
                -- Boolean i.e. true or false
                -- String i.e. 'Hello World'

            -- You can create instances with the following syntax:
            !new <instance type>('<instance name>')
            -- Example:
            !new Client('client1')
            !new Store('store4')

            -- You can assign values to attributes for a created instance with the following syntax:
            !<instance name>.<attribute name> := <value>
            -- Example for different data types:
            !client1.clientId := 1 -- For Integer
            !client1.balance := 1123.45 -- For Real
            !client1.name := 'John' -- For Strings
            !store4.available := true -- For Boolean

            -- You can create associations between instances with the following syntax:
            !insert (<instance name1>, <instance name2>) into <association name>
            -- Example:
            !insert (client1, store4) into ClientStore

            -- Custom data types usage:
                -- dataType Location
                -- operations
                --   Location(x : Real, y : Real)
                --   some other operations
                -- end
            -- You can create custom data types by calling the constructor directly; in this case, the constructor of Location requires two arguments: x and y of type Real. So it can be used as follows:
            !store4.location := Location(14.0, 289.0)

            -- Enums usage:
                -- enum Type { Clothes, Shoes }
            -- Can be used as follows:
            !store4.type := #Clothes
            </syntax_reference>
            """;

    String message = """
            Create an instance{{size}} for the following domain model:
            <domain_model>
            {{modelUML}}
            </domain_model>
            """;

    @SystemMessage(system)
    @UserMessage(message)
    String chat(@V("modelUML") String modelUML, @V("size") String sizePrompt);

    @SystemMessage(system)
    String chat(String message);
}