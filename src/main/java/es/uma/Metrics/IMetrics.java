package es.uma.Metrics;

public interface IMetrics {
    public abstract String getMetrics(String diagram, String genPath, String type);
    public abstract String getSummary();
}
