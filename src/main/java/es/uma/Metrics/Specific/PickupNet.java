package es.uma.Metrics.Specific;

import java.util.ArrayList;
import java.util.Map;

import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class PickupNet implements IMetrics {

    private int validAddress, validLatLon, validTwitter;
    private ArrayList<String> invalidAddresses, invalidLatLot, invalidTwitters;
    private int totalAddress, totalLatLon, totalTwitter;

    public PickupNet() {
        validAddress = 0;
        validLatLon = 0;
        validTwitter = 0;
        totalAddress = 0;
        totalLatLon = 0;
        totalTwitter = 0;

        invalidAddresses = new ArrayList<>();
        invalidLatLot = new ArrayList<>();
        invalidTwitters = new ArrayList<>();
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        String instance = Utils.readFile(instancePath);
        String addressesPattern = "!\\s*(\\w+)\\s*\\.\\s*(text|latitude|longitude)\\s*:=\\s*(.+)";
        String pairsPattern = "!\\s*insert\\s*\\(\\s*(\\w+)\\s*,\\s*(\\w+)\\s*\\)\\s*into\\s*AddressContainsGeoLocation";

        Map<String, Map<String, String>> addresses = Utilities.getMap(instance, addressesPattern);
        Map<String, String> pairs = Utilities.getPairs(instance, pairsPattern);

        ArrayList<String> twitters = Utils.match(instance, "!\\s*\\w+\\s*\\.\\s*twitterUserName\\s*:=\\s*'\\s*([^']+)\\s*'");

        System.out.println("Addresses: " + addresses);
        System.out.println("Pairs: " + pairs);
        System.out.println("Twitters: " + twitters);

        // Validate addresses
        pairs.forEach((addressId, geolocationId) -> {
            Map<String, String> address = addresses.get(addressId);
            Map<String, String> geolocation = addresses.get(geolocationId);

            if (address == null || geolocation == null) {
                return;
            }

            String addressText = address.get("text");
            Double latitude = Double.parseDouble(geolocation.get("latitude"));
            Double longitude = Double.parseDouble(geolocation.get("longitude"));

            if (addressText != null && latitude != null && longitude != null) {
                totalAddress++;
                Map<String, Boolean> results = Utilities.isValidAddress(addressText, latitude, longitude);
                if (results.get("validAddress")) {
                    validAddress++;
                    totalLatLon++;
                } else {
                    invalidAddresses.add(addressText);
                    return; // Skip valitation of Lat/Lon for invalid addresses
                }
                
                if (results.get("validLatLon")) {
                    validLatLon++;
                } else {
                    invalidLatLot.add("Invalid Lat: " + latitude + " Lon: " + longitude + " for address: " + addressText);
                }
            }
        });

        // Validate twitters
        twitters.forEach(twitter -> {
            totalTwitter++;
            if (Utils.validMatch(twitter, "^@?[a-zA-Z_][a-zA-Z0-9_]{3,14}$")) {
                validTwitter++;
            } else {
                invalidTwitters.add(twitter);
            }
        });
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof PickupNet)) {
            return;
        }
        
        PickupNet other = (PickupNet) otherMetrics;
        this.validAddress += other.validAddress;
        this.validLatLon += other.validLatLon;
        this.validTwitter += other.validTwitter;

        this.totalAddress += other.totalAddress;
        this.totalLatLon += other.totalLatLon;
        this.totalTwitter += other.totalTwitter;

        this.invalidAddresses.addAll(other.invalidAddresses);
        this.invalidLatLot.addAll(other.invalidLatLot);
        this.invalidTwitters.addAll(other.invalidTwitters);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| PickupNet | Invalid | Total | Failure (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Address", totalAddress - validAddress, totalAddress))
          .append(Utilities.formatMetricRow("Lat/Lon (out of valid addresses)", totalLatLon - validLatLon, totalLatLon))
          .append(Utilities.formatMetricRow("Twitter", totalTwitter - validTwitter, totalTwitter));

        sb.append(Utilities.getStringList("Invalid Addresses", invalidAddresses))
          .append(Utilities.getStringList("Invalid Lat/Lon", invalidLatLot))
          .append(Utilities.getStringList("Invalid Twitters", invalidTwitters));
        return sb.toString();
    }
    
    // Main for testing purposes
    public static void main(String[] args) {
        String instancePath = "./src/main/resources/instances/CoT/pickupnet/GEMINI_2_FLASH_LITE/18-03-2025--11-49-05/gen1/baseline.soil";
        PickupNet pickupNet = new PickupNet();
        pickupNet.calculate("diagramPath", instancePath);
        System.out.println(pickupNet);
    }
}
