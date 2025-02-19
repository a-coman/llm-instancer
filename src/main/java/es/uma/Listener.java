package es.uma;

import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponse;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequest;

public class Listener implements ChatModelListener {

    private String logsPath;
    public Listener(String logsPath) {
        this.logsPath = logsPath;
    }
    
    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        ChatModelRequest request = requestContext.request();
        StringBuffer sb = new StringBuffer();
        sb.append("Model: " + request.model() + "\n");
        sb.append("Temperature: " + request.temperature() + "\n");
        sb.append("Max-Tokens: " + request.maxTokens() + "\n");
        sb.append("Top-P: " + request.topP() + "\n");
        System.out.println(sb.toString());
        Utils.saveFile(sb.toString(), logsPath, "output.md");  
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        ChatModelResponse response = responseContext.response();
        StringBuffer sb = new StringBuffer();
        sb.append("Finish Reason: " + response.finishReason() + "\n");
        TokenUsage tokenUsage = response.tokenUsage();
        sb.append("Input Tokens: " + tokenUsage.inputTokenCount() + "\n");
        sb.append("Output Tokens: " + tokenUsage.outputTokenCount() + "\n");
        sb.append("Total Tokens: " + tokenUsage.totalTokenCount() + "\n\n");
        System.out.println(sb.toString());
        Utils.saveFile(sb.toString(), logsPath, "output.md");
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        Throwable error = errorContext.error();
        System.err.println("Error: " + error.getMessage());

        ChatModelRequest request = errorContext.request();
        System.out.println("Failed Request: " + request.messages());

        throw new RuntimeException(error);
    }
}