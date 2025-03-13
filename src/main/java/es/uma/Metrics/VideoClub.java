package es.uma.Metrics;

public class VideoClub implements IMetrics {

    @Override
    public void calculate(String diagramPath, String instancePath) {
        System.out.println("TODO: Calculating VideoClub metrics");
    }

    @Override
    public void calculateInvalid(String diagramPath, String instancePath) {
        calculate(diagramPath, instancePath);
    }

    @Override
    public void aggregate(IMetrics other) {
        System.out.println("TODO: Aggregating VideoClub metrics");
    }
    
}
