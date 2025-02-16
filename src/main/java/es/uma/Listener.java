package es.uma;

import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponse;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequest;

public class Listener implements ChatModelListener {

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        ChatModelRequest request = requestContext.request();
        
        System.out.println("Model: " + request.model());
        System.out.println("Temperature: " + request.temperature());
        System.out.println("Max-Tokens: " + request.maxTokens());
        System.out.println("Top-P: " + request.topP());
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        ChatModelResponse response = responseContext.response();
      
        System.out.println("Finish Reason: " + response.finishReason());
        TokenUsage tokenUsage = response.tokenUsage();
        System.out.println("Input Tokens: " + tokenUsage.inputTokenCount());
        System.out.println("Output Tokens: " + tokenUsage.outputTokenCount());
        System.out.println("Total Tokens: " + tokenUsage.totalTokenCount());
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        Throwable error = errorContext.error();
        System.err.println("Error: " + error.getMessage());

        ChatModelRequest request = errorContext.request();
        System.out.println("Failed Request: " + request.messages());
    }
}