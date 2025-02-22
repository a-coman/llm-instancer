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
            case "gpt-4o": 
                return OpenAiChatModel.builder()
                    .apiKey(System.getenv("OPENAI_KEY"))
                    .modelName("gpt-4o")
                    .listeners(List.of(new Listener()))
                    .build();
            case "gpt-o3-mini":
                return OpenAiChatModel.builder()
                    .apiKey(System.getenv("OPENAI_KEY"))
                    .modelName("o3-mini-2025-01-31")
                    .listeners(List.of(new Listener()))
                    .build();
            case "deepseek-v3":
                return OpenAiChatModel.builder()
                    .baseUrl("https://api.deepseek.com")
                    .apiKey(System.getenv("DEEPSEEK_KEY"))
                    .modelName("deepseek-chat")
                    .listeners(List.of(new Listener()))
                    .build();
            case "deepseek-r3":
                return OpenAiChatModel.builder()
                    .baseUrl("https://api.deepseek.com")
                    .apiKey(System.getenv("DEEPSEEK_KEY"))
                    .modelName("deepseek-reasoner")
                    .listeners(List.of(new Listener()))
                    .build();
            case "gemini-2-pro":
                return GoogleAiGeminiChatModel.builder()
                    .apiKey(System.getenv("GEMINI_KEY"))
                    .modelName("gemini-2.0-pro-exp-02-05")
                    .listeners(List.of(new Listener()))
                    .build();
            case "gemini-2-reasoner":
                return GoogleAiGeminiChatModel.builder()
                    .apiKey(System.getenv("GEMINI_KEY"))
                    .modelName("gemini-2.0-flash-thinking-exp-01-21")
                    .listeners(List.of(new Listener()))
                    .build();
            case "gemini-2-flash-lite":
                return GoogleAiGeminiChatModel.builder()
                    .apiKey(System.getenv("GEMINI_KEY"))
                    .modelName("gemini-2.0-flash-lite-preview-02-05")
                    .listeners(List.of(new Listener()))
                    .build();
            default:
                throw new IllegalArgumentException("Invalid model name: " + name);
        }
    }

}
