package es.uma;

import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponse;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequest;

public class Listener implements ChatModelListener {

    public static String logsPath;
    
    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        ChatModelRequest request = requestContext.request();
        StringBuffer sb = new StringBuffer();
        
        sb.append("\n# Input\n");
        sb.append("|Messages|\n|---|\n");
        request.messages().forEach(message -> {
            if (message instanceof SystemMessage) {
                sb.append("```\n" + ((SystemMessage) message).text() + "\n```\n");
            } else if (message instanceof UserMessage) { // Because the user message is a list of all messages in chatMemmory
                UserMessage userMessage = (UserMessage) message;
                sb.append("```\n" + userMessage.contents().getLast().toString() + "\n```\n");
            }
        });
        sb.append("\n|Request|\n|---|\n");
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

        sb.append("\n|Response|\n|---|\n");
        sb.append("Finish Reason: " + response.finishReason() + "\n");
        TokenUsage tokenUsage = response.tokenUsage();
        int inputTokens = tokenUsage.inputTokenCount();
        int outputTokens = tokenUsage.outputTokenCount();
        int totalTokens = tokenUsage.totalTokenCount();
        sb.append("Input Tokens: " + inputTokens + "\n");
        sb.append("Output Tokens: " + outputTokens + "\n");
        sb.append("Total Tokens: " + totalTokens + "\n\n");
        Metrics.incrementTokens(inputTokens, outputTokens, totalTokens);
        sb.append("\n# Output\n");
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