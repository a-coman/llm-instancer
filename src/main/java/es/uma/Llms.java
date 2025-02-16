package es.uma;

import java.util.List;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class Llms {

    public static final int MAX_MESSAGES = 24;

    public static <T> T getAgent(Class<T> agent, ChatLanguageModel model) {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(MAX_MESSAGES);
        return AiServices.builder(agent)
                .chatLanguageModel(model)
                .chatMemory(memory)
                .build();
    }

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
