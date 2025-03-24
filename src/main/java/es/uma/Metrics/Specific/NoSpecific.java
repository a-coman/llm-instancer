package es.uma.Metrics.Specific;

import es.uma.Metrics.IMetrics;

public class NoSpecific implements IMetrics {
    @Override
    public void calculate(String diagramPath, String instancePath) {
        System.out.println("No specific metrics for this system");
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {}

    @Override
    public String toString() {
        return "| No specific metrics for this system | \n |---|\n";
    }
}
