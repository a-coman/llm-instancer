package es.uma.Metrics;

public abstract class Metric {
    public abstract String getSimpleMetrics(String diagramPath, String genPath);
    public abstract String getCoTMetrics(String diagramPath, String genPath);

    public String getMetrics(String diagramPath, String genPath, String type) {
        switch (type) {
            case "Simple":
                return getSimpleMetrics(diagramPath, genPath);
            case "CoT":
                return getCoTMetrics(diagramPath, genPath);
            default:
                throw new IllegalArgumentException("Invalid type in metrics: " + type);
        }
    }

    public abstract String getSummary();
}
