package es.uma;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import es.uma.CoT.IListCreator;
import es.uma.CoT.IListInstantiator;
import es.uma.CoT.IModelAnalyzer;
import es.uma.Simple.ISimple;

public class Agents {
    
    public static final int MAX_MESSAGES = 24;

    private static <T> T createAgent(Class<T> agentType, ChatLanguageModel chatModel) {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(MAX_MESSAGES);
        return AiServices.builder(agentType)
                .chatLanguageModel(chatModel)
                .chatMemory(memory)
                .build();
    }

    public static ISimple getSimple(ChatLanguageModel chatModel) {
        return createAgent(ISimple.class, chatModel);
    }

    public static IModelAnalyzer getModelAnalyzer(ChatLanguageModel chatModel) {
        return createAgent(IModelAnalyzer.class, chatModel);
    }

    public static IListCreator getListCreator(ChatLanguageModel chatModel) {
        return createAgent(IListCreator.class, chatModel);
    }

    public static IListInstantiator getListInstantiator(ChatLanguageModel chatModel) {
        return createAgent(IListInstantiator.class, chatModel);
    }
}
