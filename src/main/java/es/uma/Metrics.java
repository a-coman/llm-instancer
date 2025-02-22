package es.uma;

import java.util.ArrayList;

public class Metrics {
    private static int sumOfInputTokens = 0;
    private static int sumOfOutputTokens = 0;
    private static int sumOfTotalTokens = 0;
    private static int sumOfSyntaxErrors = -1;
    private static int sumOfCheckErrors = -1;
    private static ArrayList<String> syntaxErrors = new ArrayList<>();
    private static ArrayList<String> checkErrors = new ArrayList<>();

    public static void incrementTokens(int input, int output, int total) {
        sumOfInputTokens += input;
        sumOfOutputTokens += output;
        sumOfTotalTokens += total;
    }

    public static void incrementSyntaxErrors() {
        if (sumOfSyntaxErrors == -1) {
            sumOfSyntaxErrors = 0;
        }
        sumOfSyntaxErrors++;
    }

    public static void incrementCheckErrors() {
        if (sumOfCheckErrors == -1) {
            sumOfCheckErrors = 0;
        }
        sumOfCheckErrors++;
    }

    public static void addSyntaxError(String error) {
        syntaxErrors.add(error);
    }

    public static void addCheckError(String error) {
        checkErrors.add(error);
    }

    public static void save(String path) {

        StringBuilder metrics = new StringBuilder();
        metrics.append("\n# Metrics\n## Sum of parameters\n||\n|---|\n");
        metrics.append("sumOfInputTokens: " + sumOfInputTokens + "\n");
        metrics.append("sumOfOutputTokens: " + sumOfOutputTokens + "\n");
        metrics.append("sumOfTotalTokens: " + sumOfTotalTokens + "\n");
        metrics.append("sumOfSyntaxErrors: " + sumOfSyntaxErrors + "\n");
        metrics.append("sumOfCheckErrors: " + sumOfCheckErrors + "\n");
        metrics.append("\n## Syntax errors\n||\n|---|\n");
        syntaxErrors.forEach(error -> metrics.append(error + "\n"));
        metrics.append("\n## Check errors\n");
        checkErrors.forEach(error -> metrics.append("```\n" + error + "\n```\n"));

        Utils.saveFile(metrics.toString(), path, "output.md");
    }
}
