package es.uma;

public class Main {
    public static void main(String[] args) {
        Experiment experiment = new Experiment("Simple", "gemini-2-flash-lite", "bank", 5);
        experiment.run();
    }
}
