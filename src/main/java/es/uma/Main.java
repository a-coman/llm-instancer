package es.uma;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

public class Main {

    private static String readFileToString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
            return "";
        }
    }

    private static String loadTemplate(String promptPath) {

        String messageTemplate = readFileToString("./src/main/resources/messageTemplate.txt");
        String diagram = readFileToString(promptPath + "diagram.use"); 
        messageTemplate = messageTemplate.replace("{diagram}", diagram);

        String examplePath = promptPath + "examples/";
        File[] filesList = new File(examplePath).listFiles();
        if (filesList != null) {
            for (int i = 0; i < filesList.length; i++) {
                File file = filesList[i];
                if (file.isFile()) {
                    String example = readFileToString(examplePath + file.getName());
                    messageTemplate = messageTemplate.replace("{example}", example);        
                    if (i < filesList.length - 1) {
                        messageTemplate = messageTemplate + "another example:\n \"\n{example}\n\" ";
                    }
                }
            }
        }
        
        // Consider StringBuilder if multiple replaces //

        return messageTemplate;
    }

    private static void saveInstance(String instance, String model, String number) {
        String filePath = "./src/main/resources/instances/bank/" + model + "_" + number + ".soil";
        try (FileWriter writer = new FileWriter(filePath)) { 
            writer.write(instance);
            System.out.println("Response saved at " + filePath);
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

        String messageTemplate = loadTemplate("./src/main/resources/prompts/bank/");
        String prompt = messageTemplate + "\n Create instances of the conceptual model.";

        ModelInstantiator modelInstantiator = AiServices.create(ModelInstantiator.class, gemini);
        String instance = modelInstantiator.chat(prompt);
        System.out.println("Instance 1:");
        System.out.println(instance);
        saveInstance(instance, "gemini", "1");

        for (int i = 2; i <= 5; i++) {
            prompt = messageTemplate + "\n Create more instances of the conceptual model.";
            instance = modelInstantiator.chat(prompt);
            System.out.println("Instance " + i + ":");
            System.out.println(instance);
            saveInstance(instance, "gemini", String.valueOf(i));
        }
 

    }

}