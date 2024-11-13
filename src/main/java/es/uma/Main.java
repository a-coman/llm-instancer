package es.uma;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class Main {
    public static void main(String[] args) {
        String apiKey = "demo";

        // Create an instance of OpenAI
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .build();

        String answer = model.generate("Say 'Hello World'");
        System.out.println(answer);

    }
}