package es.uma;

public enum Size {
    NONE(""),
    SMALL(" with around 10 to 15 objects"),
    MEDIUM(" with around 16 to 50 objects"),
    LARGE(" with around 51 to 100 objects");

    private final String prompt;

    private Size(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
