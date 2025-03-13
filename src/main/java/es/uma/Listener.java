package es.uma;

import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.ChatResponseMetadata;
import dev.langchain4j.model.output.TokenUsage;

import java.util.List;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;

public class Listener implements ChatModelListener {

    private static double startTime;

    private static ThreadLocal<String> currentAgent = new ThreadLocal<>();
    public static void setCurrentAgent(String agentName) {
        currentAgent.set(agentName);
    }

    private static ThreadLocal<String> currentCategory = new ThreadLocal<>();
    public static void setCurrentCategory(String category) {
        currentCategory.set(category);
    }

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        startTime = System.nanoTime();
        ChatRequest request = requestContext.chatRequest();
        StringBuffer sb = new StringBuffer();
        
        sb.append("# Input " + (currentAgent.get() != null ? currentAgent.get() : "Unknown") + (currentCategory.get() != null ? (" : " + currentCategory.get()) : "") + "\n");
        sb.append("|Messages|\n|---|\n");
        List<ChatMessage> messages = request.messages();
        ChatRequestParameters parameters = request.parameters();
        sb.append("```\n" + Utils.removeBackticks(messages.getFirst().toString()) + "\n```\n"); // System message (without backticks codeblock)
        sb.append("```\n" + Utils.removeBackticks(messages.getLast().toString()) + "\n```\n"); // Last user message (no prveious history) (without backticks codeblock)
        sb.append("\n|Request|\n|---|\n");
        sb.append("Model: " + parameters.modelName() + "\n");
        sb.append("Max-Tokens: " + parameters.maxOutputTokens() + "\n");
        sb.append("Temperature: " + parameters.temperature() + "\n");
        sb.append("Top-P: " + parameters.topP() + "\n\n");
        
        Logger.addLog(sb.toString());
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        ChatResponse response = responseContext.chatResponse();
        StringBuffer sb = new StringBuffer();

        ChatResponseMetadata metadata = response.metadata();

        sb.append("# Output " + (currentAgent.get() != null ? currentAgent.get() : "Unknown") + (currentCategory.get() != null ? (" : " + currentCategory.get()) : "") + "\n");
        String aiMessage = response.aiMessage().text();
        aiMessage = Utils.removeBackticks(aiMessage); // (without backticks codeblock)
        sb.append("```\n" + aiMessage + "\n```\n");
        sb.append("\n|Response|\n|---|\n");
        sb.append("Finish Reason: " + metadata.finishReason() + "\n");
        TokenUsage tokenUsage = metadata.tokenUsage();
        int inputTokens = tokenUsage.inputTokenCount();
        int outputTokens = tokenUsage.outputTokenCount();
        int totalTokens = tokenUsage.totalTokenCount();
        sb.append("Input Tokens: " + inputTokens + "\n");
        sb.append("Output Tokens: " + outputTokens + "\n");
        sb.append("Total Tokens: " + totalTokens + "\n");
        double genTime = (System.nanoTime() - startTime) / 1000000000;
        Logger.inecrementGenTime(genTime);
        sb.append("Generation Time: " + String.format("%.2f", genTime) + " seconds\n\n");
        Logger.incrementTokens(inputTokens, outputTokens, totalTokens);
        
        Logger.addLog(sb.toString());
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        Throwable error = errorContext.error();
        System.err.println("Error: " + error.getMessage());

        ChatRequest request = errorContext.chatRequest();
        System.out.println("Failed Request: " + request.messages());

        throw new RuntimeException(error);
    }
}