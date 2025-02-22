package es.uma;

import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponse;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequest;

public class Listener implements ChatModelListener {

    public static String logsPath;
    
    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        ChatModelRequest request = requestContext.request();
        StringBuffer sb = new StringBuffer();
        
        sb.append("# Input\n");
        sb.append("|Messages|\n|---|\n");
        request.messages().forEach(message -> sb.append("```\n" + message + "\n```\n"));
        sb.append("|Parameters|\n|---|\n");
        sb.append("Model: " + request.model() + "\n");
        sb.append("Temperature: " + request.temperature() + "\n");
        sb.append("Max-Tokens: " + request.maxTokens() + "\n");
        sb.append("Top-P: " + request.topP() + "\n");
        Utils.saveFile(sb.toString(), logsPath, "output.md");  
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        ChatModelResponse response = responseContext.response();
        StringBuffer sb = new StringBuffer();
        
        sb.append("Finish Reason: " + response.finishReason() + "\n");
        TokenUsage tokenUsage = response.tokenUsage();
        int inputTokens = tokenUsage.inputTokenCount();
        int outputTokens = tokenUsage.outputTokenCount();
        int totalTokens = tokenUsage.totalTokenCount();
        sb.append("Input Tokens: " + inputTokens + "\n");
        sb.append("Output Tokens: " + outputTokens + "\n");
        sb.append("Total Tokens: " + totalTokens + "\n\n");
        Metrics.incrementTokens(inputTokens, outputTokens, totalTokens);
        sb.append("# Output\n");
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