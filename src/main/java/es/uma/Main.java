package es.uma;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

public class Main {

    private static String loadToString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            return "";
        }
    }

    private static String loadTemplate() {
        String messageTemplate = loadToString(".\\src\\main\\resources\\messageTemplate.txt");
        String diagram = loadToString(".\\src\\main\\resources\\diagram.use");
        String example = loadToString(".\\src\\main\\resources\\example.soil");
        
        // Consider StringBuilder if multiple replaces //
        messageTemplate = messageTemplate.replace("{diagram}", diagram);
        messageTemplate = messageTemplate.replace("{example}", example);

        return messageTemplate;
    }

    private static void saveInstance(String response) {
        try (FileWriter writer = new FileWriter(".\\src\\main\\resources\\instance.soil")){
            writer.write(response);
            System.out.println("Response saved to instance.soil");
        } catch (Exception e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    
    interface ModelInstantiator {     
        @SystemMessage(fromResource = "systemMessage.txt")
        String chat(String userMessage);
    }

    public static void main(String[] args) {

        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
        .apiKey(System.getenv("GEMINI_KEY"))
        .modelName("gemini-1.5-flash")
        .build();

        String userMessage = loadTemplate();

        ModelInstantiator modelInstantiator = AiServices.create(ModelInstantiator.class, gemini);
        String response = modelInstantiator.chat(userMessage);

        System.out.println("Response:");
        System.out.println(response);
        saveInstance(response);

    }

}