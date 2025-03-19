package es.uma.Metrics.Specific;

import java.util.Map;
import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class AddressBook implements IMetrics {

    private int validPhone, validWebsite, validEmail, validAddress;
    private int totalPhone, totalWebsite, totalEmail, totalAddress;
    
    public AddressBook() {
        validPhone = 0;
        validWebsite = 0;
        validEmail = 0;
        validAddress = 0;
        totalPhone = 0;
        totalWebsite = 0;
        totalEmail = 0;
        totalAddress = 0;
    }

    // TODO: Can be simplified to just Utilities.match as we dont need to map one with the other (no pairs)
    @Override
    public void calculate(String diagramPath, String instancePath) {
        String instance = Utils.readFile(instancePath);
        String addressesPattern = "!\\s*(\\w+)\\s*\\.\\s*(city|houseNr|street)\\s*:=\\s*(.+)";
        String contactsPattern = "!\\s*(\\w+)\\s*\\.\\s*(phone|website|email)\\s*:=\\s*(.+)"; 
        Map<String, Map<String, String>> addresses = Utilities.getMap(instance, addressesPattern);
        Map<String, Map<String, String>> contacts = Utilities.getMap(instance, contactsPattern);
        
        System.out.println("Addresses: " + addresses);
        System.out.println("Contacts: " + contacts);

        // Validate addresses
        addresses.forEach((address, attributes) -> {
            String city = attributes.get("city");
            String houseNr = attributes.get("houseNr");
            String street = attributes.get("street");

            if (city == null || houseNr == null || street == null) {
                return;
            }

            totalAddress++;
            if (Utilities.isValidAddress(city, street + ", " + houseNr)) {
                validAddress++;
            }
        });


        // Validate contacts
        contacts.forEach((contact, attributes) -> {
           String phone = attributes.get("phone");
           String website = attributes.get("website");
           String email = attributes.get("email");

           if (phone != null) {
                totalPhone++;
                if (Utilities.isValidPhone(phone)) {
                    validPhone++;
                }
           }

           if (website != null) {
                totalWebsite++;
                if (Utilities.isValidWebsite(website)) {
                    validWebsite++;
                }
           }

           if (email != null) {
                totalEmail++;
                if (Utilities.isValidEmail(email)) {
                    validEmail++;
                }
           }

        });

        
    }

    @Override
    public void calculateInvalid(String diagramPath, String instancePath) {
        calculate(diagramPath, instancePath);
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof AddressBook)) {
            return;
        }
        
        AddressBook other = (AddressBook) otherMetrics;
        this.validAddress += other.validAddress;
        this.validPhone += other.validPhone;
        this.validWebsite += other.validWebsite;
        this.validEmail += other.validEmail;
        
        this.totalAddress += other.totalAddress; 
        this.totalPhone += other.totalPhone;
        this.totalWebsite += other.totalWebsite;
        this.totalEmail += other.totalEmail;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| AddressBook | Valid | Total | Success (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Phones", validPhone, totalPhone))
          .append(Utilities.formatMetricRow("Websites", validWebsite, totalWebsite))
          .append(Utilities.formatMetricRow("Emails", validEmail, totalEmail))
          .append(Utilities.formatMetricRow("Addresses", validAddress, totalAddress));
        return sb.toString();
    }

    // Main for testing purposes
    public static void main(String[] args) {
        String instancePath = "./src/main/resources/instances/CoT/addressbook/GPT_4O/17-03-2025--18-01-20/gen1/baseline.soil";
        AddressBook addressBook = new AddressBook();
        addressBook.calculate("diagramPath", instancePath);
        System.out.println(addressBook);
    }
}
