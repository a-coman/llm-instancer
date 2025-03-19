package es.uma.Metrics.Specific;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    private static Map<String, String> getPairs(String instance) {
        Map<String, String> pairs = new HashMap<>();

        Pattern pattern = Pattern.compile("!\\s*insert\\s*\\(\\s*(\\w+)\\s*,\\s*(\\w+)\\s*\\)\\s*into\\s*AddressContainsGeoLocation");
        Matcher matcher = pattern.matcher(instance);

        while (matcher.find()) {
            String addressId = matcher.group(1);
            String geolocationId = matcher.group(2);
            pairs.put(addressId, geolocationId);
        }

        return pairs;
    }

    private static Map<String, Map<String, String>> getAddresses(String instance) {
        Map<String, Map<String, String>> addresses = new HashMap<>();

        String textPattern = "!\\s*(\\w+)\\s*\\.\\s*(text|latitude|longitude)\\s*:=\\s*(.+)";
        Pattern p = Pattern.compile(textPattern);
        Matcher m = p.matcher(instance);
        while (m.find()) {
            String entity = m.group(1);
            String attribute = m.group(2);
            String value = m.group(3).replace("'", "");
            
            addresses.putIfAbsent(entity, new HashMap<>());
            addresses.get(entity).put(attribute, value);
        }

        return addresses;
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        String instance = Utils.readFile(instancePath);
        Map<String, Map<String, String>> addresses = getAddresses(instance);
        Map<String, String> pairs = getPairs(instance);
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
