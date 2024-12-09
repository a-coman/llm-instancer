package es.uma;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

public class Main {
    
    private static IModelAnalyzer modelAnalyzer;
    private static IListCreator listCreator;
    private static IModelInstantiator modelInstantiator;

    private static void initializeAgents() {
        ChatLanguageModel chatGPT = OpenAiChatModel.builder()
        .apiKey(System.getenv("OPEN_API_KEY"))
        .modelName("gpt-4o")
        .build();

        ChatMemory analyzerMemory = MessageWindowChatMemory.withMaxMessages(10);
        modelAnalyzer = AiServices.builder(IModelAnalyzer.class)
        .chatLanguageModel(chatGPT)
        .chatMemory(analyzerMemory)
        .build();

        ChatMemory listCreatorMemory = MessageWindowChatMemory.withMaxMessages(10);
        listCreator = AiServices.builder(IListCreator.class)
        .chatLanguageModel(chatGPT)
        .chatMemory(listCreatorMemory)
        .build();

        ChatMemory instantiatorMemory = MessageWindowChatMemory.withMaxMessages(10);
        modelInstantiator = AiServices.builder(IModelInstantiator.class)
        .chatLanguageModel(chatGPT)
        .chatMemory(instantiatorMemory)
        .build();
    }

    public static void main(String[] args) {
        initializeAgents();

    }


}