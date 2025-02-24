package es.uma;

public class Main {
    public static void main(String[] args) {
        Experiment experiment = new Experiment(Model.GPT_4O, "CoT", "bank", 1);
        experiment.run();
    }
}
