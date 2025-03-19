package es.uma.Metrics.Specific;

import java.util.ArrayList;
import java.util.Map;

import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class PickupNet implements IMetrics {

    private int validAddress, validTwitter;
    private int totalAddress, totalTwitter;

    public PickupNet() {
        validAddress = 0;
        validTwitter = 0;
        totalAddress = 0;
        totalTwitter = 0;
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
                if (Utilities.isValidAddress(addressText, latitude, longitude)) {
                    validAddress++;
                }
            }
        });

        // Validate twitters
        twitters.forEach(twitter -> {
            totalTwitter++;
            if (Utils.validMatch(twitter, "^@?[a-zA-Z_][a-zA-Z0-9_]{3,14}$")) {
                validTwitter++;
            }
        });
    }

    @Override
    public void calculateInvalid(String diagramPath, String instancePath) {
        calculate(diagramPath, instancePath);
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof PickupNet)) {
            return;
        }
        
        PickupNet other = (PickupNet) otherMetrics;
        this.validAddress += other.validAddress;
        this.validTwitter += other.validTwitter;

        this.totalAddress += other.totalAddress;
        this.totalTwitter += other.totalTwitter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| PickupNet | Valid | Total | Success (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Address", validAddress, totalAddress))
          .append(Utilities.formatMetricRow("Twitter", validTwitter, totalTwitter));
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
