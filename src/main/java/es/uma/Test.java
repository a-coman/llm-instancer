package es.uma;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

public class Test {

    interface ITestAgent {
        String system = """
                You are tasked with creating a 2D diagram in Python based on a provided instance of components, their positions, connections, and relationships. Follow these requirements carefully:
                - Represent each component as a colored bullet point corresponding to its type or label.
                - Label each bullet point with its name or identifier, displayed near the bullet.
                - Draw conveyors between components to indicate connections (as defined in the instance).
                - The color of the conveyors must match the color of the starting bullet point.
                - If conveyors overlap shift them introducing an perpendicular offset gap (its okey if they do not longer connect with the points).
                - Include an x-axis and y-axis to represent the positions of components. 
                - Scale the axis accordingly to ensure full visibility of the diagram.
                - Use grid lines to make it easier to read the positions of component.
                - The bulletpoints and arrows/conveyors should be small.
                """;
        @SystemMessage(system)
        String chat(String message);
    }

    public static void main(String[] args) {
        // Initialize the agents
        ChatLanguageModel chatGPT = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPEN_API_KEY"))
            .modelName("gpt-4o")
            .build();

        ITestAgent agent = AiServices.builder(ITestAgent.class)
            .chatLanguageModel(chatGPT)
            .build();
        
        //#region message
        String message = """
                Lets start with this one:

                Instance 1: Simple Assembly Line
                Elements
                HandleGenerator_A

                Type: HandleGenerator
                Attributes:
                x: 10
                y: 20
                processingTime: 5
                counter: 100
                Relationships: Outputs to Tray_A
                HeadGenerator_B

                Type: HeadGenerator
                Attributes:
                x: 30
                y: 20
                processingTime: 7
                counter: 150
                Relationships: Outputs to Tray_B
                Assembler_C

                Type: Assembler
                Attributes:
                x: 50
                y: 20
                processingTime: 10
                Relationships:
                Inputs from Tray_A, Tray_B
                Outputs to Tray_C
                Polisher_D

                Type: Polisher
                Attributes:
                x: 70
                y: 20
                processingTime: 8
                Relationships:
                Inputs from Tray_C
                Outputs to Tray_D
                Tray_A

                Type: Tray
                Attributes:
                x: 15
                y: 25
                capacity: 50
                Content: [Handle, Handle, ..., Handle] (up to capacity)
                Tray_B

                Type: Tray
                Attributes:
                x: 35
                y: 25
                capacity: 50
                Content: [Head, Head, ..., Head] (up to capacity)
                Tray_C

                Type: Tray
                Attributes:
                x: 55
                y: 25
                capacity: 20
                Content: [Hammer, Hammer, ..., Hammer] (up to capacity)
                Tray_D

                Type: Tray
                Attributes:
                x: 75
                y: 25
                capacity: 20
                Content: [Hammer (isPolished: True), Hammer (isPolished: True), ..., Hammer] (up to capacity)
                Pieces
                Handle

                Type: Handle
                Attributes:
                Generic attributes since specific ones are not defined in the abstract class.
                Head

                Type: Head
                Attributes:
                Generic attributes since specific ones are not defined in the abstract class.
                Hammer

                Type: Hammer
                Attributes:
                isPolished: False (in Tray_C), True (in Tray_D)
                """;
        //#endregion

        System.out.println(agent.chat(message));
    }
}
