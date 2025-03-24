package es.uma.Metrics;

public interface IMetrics {
    // Calculate metrics from diagram and instance
    public void calculate(String diagramPath, String instancePath);
    
    // Aggregate metrics from another metrics object of the same type
    public void aggregate(IMetrics other);
    
    // Get string representation of metrics
    public String toString();

}
