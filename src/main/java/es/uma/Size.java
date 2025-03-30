package es.uma;

public enum Size {
    NONE(""),
    MINIMAL(" with 10 or more objects"),
    SMALL(" with 20 or more objects"),
    MEDIUM(" with 30 or more objects"),
    LARGE(" with 40 or more objects"),
    GIANT(" with 50 or more objects");

    private final String prompt;

    private Size(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
