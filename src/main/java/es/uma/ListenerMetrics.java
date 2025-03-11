package es.uma;

public class ListenerMetrics {
    private static int sumOfInputTokens = 0;
    private static int sumOfOutputTokens = 0;
    private static int sumOfTotalTokens = 0;
    private static double genTime = 0;
    private static Experiment experiment;

    public static void inecrementGenTime(double time) {
        genTime += time;
    }

    public static void incrementTokens(int input, int output, int total) {
        sumOfInputTokens += input;
        sumOfOutputTokens += output;
        sumOfTotalTokens += total;
    }

    public static void setExperiment(Experiment experiment) {
        ListenerMetrics.experiment = experiment;
    }

    public static void save(String path) {

        StringBuilder metrics = new StringBuilder();

        metrics.append("\n# Summary for all generations\n");
        metrics.append("| Metric | Value |\n");
        metrics.append("| --- | --- |\n");
        metrics.append("| Model | " + experiment.modelName + " |\n");
        metrics.append("| Type | " + experiment.type + " |\n");
        metrics.append("| System | " + experiment.system + " |\n");
        metrics.append("| Number of generations | " + experiment.repetitions + " |\n");
        metrics.append("| Generations time | " + String.format("%.2f", genTime) + " seconds |\n");
        metrics.append("| Sum of input tokens | " + sumOfInputTokens + " |\n");
        metrics.append("| Sum of output tokens | " + sumOfOutputTokens + " |\n");
        metrics.append("| Sum of total tokens | " + sumOfTotalTokens + " |\n");

        Utils.saveFile(metrics.toString(), path, "output.md");
    }
}
