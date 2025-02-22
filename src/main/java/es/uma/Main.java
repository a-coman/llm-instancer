package es.uma;

public class Main {
    public static void main(String[] args) {
        Experiment experiment = new Experiment("CoT", "gemini-2-lite", "bank");
        experiment.run();
    }
}
