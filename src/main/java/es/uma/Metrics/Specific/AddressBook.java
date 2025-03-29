package es.uma.Metrics.Specific;

import java.util.ArrayList;
import java.util.Map;
import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class AddressBook implements IMetrics {

    private int validPhone, validWebsite, validEmail, validAddress;
    private ArrayList<String> invalidPhones, invalidWebsites, invalidEmails, invalidAddresses;
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

        invalidAddresses = new ArrayList<>();
        invalidPhones = new ArrayList<>();
        invalidWebsites = new ArrayList<>();
        invalidEmails = new ArrayList<>();
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
            if (Utilities.isValidAddress(city, street, houseNr)) {
                validAddress++;
            } else {
                invalidAddresses.add(street + ", " + houseNr + ", " + city); 
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
                } else {
                    invalidPhones.add(phone);
                }
           }

           if (website != null) {
                totalWebsite++;
                if (Utilities.isValidWebsite(website)) {
                    validWebsite++;
                } else {
                    invalidWebsites.add(website);
                }
           }

           if (email != null) {
                totalEmail++;
                if (Utilities.isValidEmail(email)) {
                    validEmail++;
                } else {
                    invalidEmails.add(email);
                }
           }

        });

        
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

        this.invalidAddresses.addAll(other.invalidAddresses);
        this.invalidPhones.addAll(other.invalidPhones);
        this.invalidWebsites.addAll(other.invalidWebsites);
        this.invalidEmails.addAll(other.invalidEmails);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| AddressBook | Invalid | Total | Failure (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Phones", totalPhone - validPhone, totalPhone))
          .append(Utilities.formatMetricRow("Websites", totalWebsite - validWebsite, totalWebsite))
          .append(Utilities.formatMetricRow("Emails", totalEmail - validEmail, totalEmail))
          .append(Utilities.formatMetricRow("Addresses", totalAddress - validAddress, totalAddress));

        sb.append(Utilities.getStringList("Invalid Phones", invalidPhones))
          .append(Utilities.getStringList("Invalid Websites", invalidWebsites))
          .append(Utilities.getStringList("Invalid Emails", invalidEmails))
          .append(Utilities.getStringList("Invalid Addresses", invalidAddresses));
        
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
