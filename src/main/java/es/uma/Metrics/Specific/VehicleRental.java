package es.uma.Metrics.Specific;

import java.util.ArrayList;

import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class VehicleRental implements IMetrics{

    private int validAddress, validLicensePlate, validHomePhone;
    private ArrayList<String> invalidAddresses, invalidLicensePlates, invalidHomePhones;
    private int totalAddress, totalLicensePlate, totalHomePhone;

    public VehicleRental() {
        validAddress = 0;
        validLicensePlate = 0;
        validHomePhone = 0;
        totalAddress = 0;
        totalLicensePlate = 0;
        totalHomePhone = 0;
        invalidAddresses = new ArrayList<>();
        invalidLicensePlates = new ArrayList<>();
        invalidHomePhones = new ArrayList<>();
    }

    private static Boolean isValidLicensePlate(String licensePlate) {
        String pattern = "^[A-Z0-9][A-Z0-9\\s-]{1,9}[A-Z0-9]$";
        return Utils.split(licensePlate, pattern).isEmpty() ? false : true;
    }


    @Override
    public void calculate(String diagramPath, String instancePath) {
        String instance = Utils.readFile(instancePath);
        ArrayList<String> addresses = Utils.match(instance, "!\\s*\\w+\\s*\\.\\s*address\\s*:=\\s*'\\s*([^']+)\\s*'");
        ArrayList<String> licensePlates = Utils.match(instance, "!\\s*\\w+\\s*\\.\\s*licensePlateNumber\\s*:=\\s*'\\s*([^']+)\\s*'");
        ArrayList<String> homePhones = Utils.match(instance, "!\\s*\\w+\\s*\\.\\s*homePhone\\s*:=\\s*'\\s*([^']+)\\s*'");
        
        System.out.println("Addresses: " + addresses);
        System.out.println("License Plates: " + licensePlates);
        System.out.println("Home Phones: " + homePhones);

        addresses.forEach((address) -> {
            totalAddress++;
            if (Utilities.isValidAddress(address)) {
                validAddress++;
            } else {
                invalidAddresses.add(address);
            }
        });

        licensePlates.forEach((licensePlate) -> {
            totalLicensePlate++;
            if (isValidLicensePlate(licensePlate)) {
                validLicensePlate++;
            } else {
                invalidLicensePlates.add(licensePlate);
            }
        });

        homePhones.forEach((homePhone) -> {
            totalHomePhone++;
            if (Utilities.isValidPhone(homePhone)) {
                validHomePhone++;
            } else {
                invalidHomePhones.add(homePhone);
            }
        });
        
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof VehicleRental)) {
            return;
        }
        
        VehicleRental other = (VehicleRental) otherMetrics;
        this.validAddress += other.validAddress;
        this.validLicensePlate += other.validLicensePlate;
        this.validHomePhone += other.validHomePhone;

        this.totalAddress += other.totalAddress;
        this.totalLicensePlate += other.totalLicensePlate;
        this.totalHomePhone += other.totalHomePhone;

        this.invalidAddresses.addAll(other.invalidAddresses);
        this.invalidLicensePlates.addAll(other.invalidLicensePlates);
        this.invalidHomePhones.addAll(other.invalidHomePhones);

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| VehicleRental | Invalid | Total | Failure (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Addresses", totalAddress - validAddress, totalAddress))
          .append(Utilities.formatMetricRow("License Plates", totalLicensePlate - validLicensePlate, totalLicensePlate))
          .append(Utilities.formatMetricRow("Home Phones", totalHomePhone - validHomePhone, totalHomePhone));

        sb.append(Utilities.getStringList("Invalid Addresses", invalidAddresses))
          .append(Utilities.getStringList("Invalid License Plates", invalidLicensePlates))
          .append(Utilities.getStringList("Invalid Home Phones", invalidHomePhones));
        return sb.toString();
    }

    // Main for testing purposes
    public static void main(String[] args) {
        String instancePath = "./src/main/resources/instances/CoT/vehiclerental/GPT_4O/18-03-2025--23-26-55/gen1/baseline.soil";
        VehicleRental vehicleRental = new VehicleRental();
        vehicleRental.calculate("diagramPath", instancePath);
        System.out.println(vehicleRental);
    }
    
}
