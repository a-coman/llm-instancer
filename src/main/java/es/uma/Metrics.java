package es.uma;

public class Metrics {
    private static int sumOfInputTokens = 0;
    private static int sumOfOutputTokens = 0;
    private static int sumOfTotalTokens = 0;
    private static int syntaxErrors = -1;
    private static int checkErrors = -1;

    public static void incrementTokens(int input, int output, int total) {
        sumOfInputTokens += input;
        sumOfOutputTokens += output;
        sumOfTotalTokens += total;
    }

    public static void incrementSyntaxErrors() {
        if (syntaxErrors == -1) {
            syntaxErrors = 0;
        }
        syntaxErrors++;
    }

    public static void incrementCheckErrors() {
        if (checkErrors == -1) {
            checkErrors = 0;
        }
        checkErrors++;
    }

    public static void save(String path) {

        StringBuilder metrics = new StringBuilder();
        metrics.append("\n# Metrics\n|Metric|\n|---|\n");
        metrics.append("sumOfInputTokens: " + sumOfInputTokens + "\n");
        metrics.append("sumOfOutputTokens: " + sumOfOutputTokens + "\n");
        metrics.append("sumOfTotalTokens: " + sumOfTotalTokens + "\n");
        metrics.append("syntaxErrors: " + syntaxErrors + "\n");
        metrics.append("checkErrors: " + checkErrors + "\n");

        Utils.saveFile(metrics.toString(), path, "output.md");
    }
}
