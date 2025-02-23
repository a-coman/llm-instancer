package es.uma;

public class Main {
    public static void main(String[] args) {
        Experiment experiment = new Experiment("CoT", "gpt-4o", "bank", 1);
        experiment.run();
    }
}
