package es.uma;

import java.util.List;
import java.util.ArrayList;

public class Logger {
    private static int sumOfInputTokens = 0;
    private static int sumOfOutputTokens = 0;
    private static int sumOfTotalTokens = 0;
    private static double genTime = 0;
    private static Experiment experiment;
    private static List<String> logs = new ArrayList<>();

    public static void addLog(String log) {
        logs.add(log);
    }

    public static void inecrementGenTime(double time) {
        genTime += time;
    }

    public static void incrementTokens(int input, int output, int total) {
        sumOfInputTokens += input;
        sumOfOutputTokens += output;
        sumOfTotalTokens += total;
    }

    public static void setExperiment(Experiment experiment) {
        Logger.experiment = experiment;
    }

    private static String getListenerLogs() {
        StringBuilder listenerLogs = new StringBuilder();
        for (String log : logs) { 
            listenerLogs.append(log);
        }
        return listenerLogs.toString();
    }

    public static void save(String path) {

        StringBuilder metrics = new StringBuilder();
        metrics.append(getListenerLogs());

        metrics.append("\n# Summary for all generations\n");
        metrics.append("| Metric | Value |\n");
        metrics.append("| --- | --- |\n");
        metrics.append("| Model | " + experiment.getModelName() + " |\n");
        metrics.append("| Type | " + experiment.getType() + " |\n");
        metrics.append("| System | " + experiment.getSystem() + " |\n");
        metrics.append("| Context window tokens | " + Llms.MAX_TOKENS + " |\n");
        metrics.append("| Number of generations | " + experiment.getRepetitions() + " |\n");
        metrics.append("| Generations time | " + String.format("%.2f", genTime) + " seconds |\n");
        metrics.append("| Sum of input tokens | " + sumOfInputTokens + " |\n");
        metrics.append("| Sum of output tokens | " + sumOfOutputTokens + " |\n");
        metrics.append("| Sum of total tokens | " + sumOfTotalTokens + " |\n");

        Utils.saveFile(metrics.toString(), path, "logs.md");
    }
}
