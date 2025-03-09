package es.uma;

public class ListenerMetrics {
    private static int sumOfInputTokens = 0;
    private static int sumOfOutputTokens = 0;
    private static int sumOfTotalTokens = 0;
    private static double genTime = 0;
    public static int repetitions;

    public static void inecrementGenTime(double time) {
        genTime += time;
    }

    public static void incrementTokens(int input, int output, int total) {
        sumOfInputTokens += input;
        sumOfOutputTokens += output;
        sumOfTotalTokens += total;
    }

    public static void setRepetitions(int repetitions) {
        ListenerMetrics.repetitions = repetitions;
    }

    public static void save(String path) {

        StringBuilder metrics = new StringBuilder();

        metrics.append("\n # Summary\n");
        metrics.append("| Metric | Value |\n");
        metrics.append("| --- | --- |\n");
        metrics.append("| Number of generations | " + repetitions + " |\n");
        metrics.append("| Generations time | " + genTime + " seconds |\n");
        metrics.append("| Sum of input tokens | " + sumOfInputTokens + " |\n");
        metrics.append("| Sum of output tokens | " + sumOfOutputTokens + " |\n");
        metrics.append("| Sum of total tokens | " + sumOfTotalTokens + " |\n");

        Utils.saveFile(metrics.toString(), path, "output.md");
    }
}
