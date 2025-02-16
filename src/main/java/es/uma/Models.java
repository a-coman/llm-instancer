package es.uma;

import java.util.List;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class Models {

    public static ChatLanguageModel getModel(String name) {
        switch (name) {
            case "4o": 
                return OpenAiChatModel.builder()
                    .apiKey(System.getenv("OPEN_API_KEY"))
                    .modelName("gpt-4o")
                    .listeners(List.of(new Listener()))
                    .build();
            case "o1":
                return OpenAiChatModel.builder()
                    .apiKey(System.getenv("OPENAI_KEY"))
                    .modelName("gpt-4o")
                    .listeners(List.of(new Listener()))
                    .build();
            case "v3":
                return OpenAiChatModel.builder()
                    .apiKey(System.getenv("DEEPSEEK_KEY"))
                    .modelName("gpt-4o")
                    .listeners(List.of(new Listener()))
                    .build();
            case "r3":
                return OpenAiChatModel.builder()
                    .apiKey(System.getenv("DEEPSEEK_KEY"))
                    .modelName("gpt-4o")
                    .listeners(List.of(new Listener()))
                    .build();
            case "g2":
                return GoogleAiGeminiChatModel.builder()
                    .apiKey(System.getenv("DEEPSEEK_KEY"))
                    .modelName("gpt-4o")
                    .listeners(List.of(new Listener()))
                    .build();
            case "g2r":
                return GoogleAiGeminiChatModel.builder()
                    .apiKey(System.getenv("DEEPSEEK_KEY"))
                    .modelName("gpt-4o")
                    .listeners(List.of(new Listener()))
                    .build();
            default:
                throw new IllegalArgumentException("Invalid model name: " + name);
        }
    }
}
