package es.uma.Metrics;

public class MetricsFactory {
    public static IMetrics createMetrics(String system) {
        switch (system.toLowerCase()) {
            case "bank":
                return new Bank();
            case "videoclub":
                return new VideoClub();
            default:
                return new NoSpecific();
        }
    }
}
