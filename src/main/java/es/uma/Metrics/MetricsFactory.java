package es.uma.Metrics;

public class MetricsFactory {
    public static IMetrics createMetrics(String system) {
        switch (system.toLowerCase()) {
            case "bank":
                return new Bank();
            // Add more cases here for other systems
            default:
                throw new IllegalArgumentException("Unknown system: " + system);
        }
    }
}
