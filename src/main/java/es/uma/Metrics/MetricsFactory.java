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
            case "pickupnet":
                return new PickupNet();
            case "addressbook":
                return new AddressBook();
            case "myexpenses":
                return new MyExpenses();
            case "restaurant":
                return new Restaurant();
            case "football":
                return new Football();
            default:
                return new NoSpecific();
        }
    }
}
