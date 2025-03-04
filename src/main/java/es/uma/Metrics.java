package es.uma;

import java.util.ArrayList;

public class Metrics {
    private static int sumOfInputTokens = 0;
    private static int sumOfOutputTokens = 0;
    private static int sumOfTotalTokens = 0;
    private static int sumOfSyntaxErrors = -1; // -1 -> agent not checking syntax
    private static int sumOfCheckErrors = -1; // -1 -> agent not checking restrictions
    private static int sumOfMultiplicitiesErrors = -1;
    private static int sumOfInvariantErrors = -1;
    private static ArrayList<String> syntaxErrors = new ArrayList<>();
    private static ArrayList<String> checkErrors = new ArrayList<>();
    private static double genTime = 0;
    public static int repetitions;

    public static void incrementTokens(int input, int output, int total) {
        sumOfInputTokens += input;
        sumOfOutputTokens += output;
        sumOfTotalTokens += total;
    }

    public static void initializeSyntax() {
        if (sumOfSyntaxErrors == -1) {
            sumOfSyntaxErrors = 0;
        }
    }

    public static void initializeCheck() {
        if (sumOfCheckErrors == -1) {
            sumOfCheckErrors = 0;
        }
    }

    public static void incrementSyntaxErrors() {
        sumOfSyntaxErrors++;
    }

    public static void incrementCheckErrors() {
        sumOfCheckErrors++;
    }

    public static void addSyntaxError(String error) {
        syntaxErrors.add(error);
    }

    public static void addCheckError(String error) {
        checkErrors.add(error);
    }

    public static void inecrementGenTime(double time) {
        genTime += time;
    }

    public static void addFailedCheck(String categoryId) {
        checkErrors.add("Failed MAX checks for category " + categoryId);
    }

    public static void incrementMultiplicitiesErrors(int num) {
        if (sumOfMultiplicitiesErrors == -1) {
            sumOfMultiplicitiesErrors = 0;
        }
        sumOfMultiplicitiesErrors += num;
    }
    
    public static void incrementInvariantErrors(int num) {
        if (sumOfInvariantErrors == -1) {
            sumOfInvariantErrors = 0;
        }
        sumOfInvariantErrors += num;
    }

    public static void save(String path) {

        StringBuilder metrics = new StringBuilder();
        metrics.append("\n# Metrics\n## Sum of parameters\n||\n|---|\n");
        metrics.append("Number of generations: " + repetitions + "\n");
        metrics.append("Sum of input tokens: " + sumOfInputTokens + "\n");
        metrics.append("Sum of output tokens: " + sumOfOutputTokens + "\n");
        metrics.append("Sum of total tokens: " + sumOfTotalTokens + "\n");
        metrics.append("Generations time: " + genTime + " seconds\n");
        metrics.append("Sum of syntax errors: " + sumOfSyntaxErrors + "\n");
        metrics.append("Nº of checks with errors: " + sumOfCheckErrors + "\n");
        metrics.append("Sum of multiplicities errors: " + sumOfMultiplicitiesErrors + "\n");
        metrics.append("Sum of invariant errors: " + sumOfInvariantErrors + "\n");

        metrics.append("\n## Syntax errors\n||\n|---|\n");
        syntaxErrors.forEach(error -> metrics.append(error + "\n"));
        metrics.append("\n## Checks with errors\n");
        checkErrors.forEach(error -> metrics.append("```\n" + error + "\n```\n"));

        Utils.saveFile(metrics.toString(), path, "output.md");
    }
}
