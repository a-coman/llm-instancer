package es.uma.Metrics;

import es.uma.Metrics.Specific.*;

public class MetricsFactory {
    public static IMetrics createMetrics(String system) {
        switch (system.toLowerCase()) {
            case "bank":
                return new Bank();
            case "videoclub":
                return new VideoClub();
            case "hotelmanagement":
                return new HotelManagement();
            case "vehiclerental":
                return new VehicleRental();
            default:
                return new NoSpecific();
        }
    }
}
