package es.uma;

import java.time.Duration;
import java.util.List;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.service.AiServices;

public class Llms {

    public static final int MAX_TOKENS = 64000;

    public static <T> T getAgent(Class<T> agent, ChatLanguageModel model) {
        Listener.setCurrentAgent(agent.getSimpleName());
        ChatMemory memory = TokenWindowChatMemory.withMaxTokens(MAX_TOKENS, new OpenAiTokenizer("gpt-4o"));
        return AiServices.builder(agent)
                .chatLanguageModel(model)
                .chatMemory(memory)
                .build();
    }

    public static ChatLanguageModel getModel(Model model) {
        switch (model) {
            case GPT_4O: 
                return OpenAiChatModel.builder()
                    .apiKey(System.getenv("OPENAI_KEY"))
                    .modelName("gpt-4o")
                    .logRequests(true)
                    .logResponses(true)
                    .listeners(List.of(new Listener()))
                    .maxCompletionTokens(16384)
                    .temperature(1.0)
                    .topP(1.0)
                    .maxRetries(10)
                    .timeout(Duration.ofSeconds(180))
                    .build();
            case GPT_O3_MINI:
                return OpenAiChatModel.builder()
                    .apiKey(System.getenv("OPENAI_KEY"))
                    .modelName("o3-mini-2025-01-31")
                    .logRequests(true)
                    .logResponses(true)
                    .listeners(List.of(new Listener()))
                    .build();
            case DEEPSEEK_V3:
                return OpenAiChatModel.builder()
                    .baseUrl("https://api.deepseek.com")
                    .apiKey(System.getenv("DEEPSEEK_KEY"))
                    .modelName("deepseek-chat")
                    .logRequests(true)
                    .logResponses(true)
                    .listeners(List.of(new Listener()))
                    .maxRetries(10)
                    .timeout(Duration.ofSeconds(180))
                    .build();
            case DEEPSEEK_R3:
                return OpenAiChatModel.builder()
                    .baseUrl("https://api.deepseek.com")
                    .apiKey(System.getenv("DEEPSEEK_KEY"))
                    .modelName("deepseek-reasoner")
                    .logRequests(true)
                    .logResponses(true)
                    .listeners(List.of(new Listener()))
                    .maxRetries(10)
                    .timeout(Duration.ofSeconds(180))
                    .build();
            case GEMINI_2_PRO:
                return GoogleAiGeminiChatModel.builder()
                    .apiKey(System.getenv("GEMINI_KEY"))
                    .modelName("gemini-2.0-pro-exp-02-05")
                    .logRequestsAndResponses(true)
                    .listeners(List.of(new Listener()))
                    .build();
            case GEMINI_2_REASONER:
                return GoogleAiGeminiChatModel.builder()
                    .apiKey(System.getenv("GEMINI_KEY"))
                    .modelName("gemini-2.0-flash-thinking-exp-01-21")
                    .logRequestsAndResponses(true)
                    .listeners(List.of(new Listener()))
                    .build();
            case GEMINI_2_FLASH_LITE:
                return GoogleAiGeminiChatModel.builder()
                    .apiKey(System.getenv("GEMINI_KEY"))
                    .maxOutputTokens(null) // Limit for testing
                    .modelName("gemini-2.0-flash-lite-preview-02-05")
                    .logRequestsAndResponses(true)
                    .listeners(List.of(new Listener()))
                    .build();
            default:
                throw new IllegalArgumentException("Invalid model name: " + model);
        }
    }

}
