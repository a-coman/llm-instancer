package es.uma.Metrics.Specific;

import java.util.ArrayList;
import java.util.List;

import de.schegge.bank.validator.BIC;
import de.schegge.bank.validator.BicValidator;
import de.schegge.bank.validator.Check;
import de.schegge.bank.validator.IBAN;
import de.schegge.bank.validator.IbanValidator;
import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class Bank implements IMetrics {

    private int realIbans, validIbans, validBics, validCountries;
    private int totalIbans, totalBics, totalCountries;
    private ArrayList<String> failedRealIbans, failedIbans, failedBics, failedCountries;

    public Bank() {
        validIbans = 0;
        realIbans = 0;
        validBics = 0;
        validCountries = 0;
        totalIbans = 0;
        totalBics = 0;
        totalCountries = 0;

        failedRealIbans = new ArrayList<>();
        failedIbans = new ArrayList<>();
        failedBics = new ArrayList<>();
        failedCountries = new ArrayList<>();
    }

    private List<String> getIbans (String instance) {
        String ibanPattern = "!\\w+\\.iban\\s*:=\\s*'(.+?)'";
        return Utils.match(instance, ibanPattern);
    }

    private List<String> getBics(String instance) {
        String bicPattern = "!\\w+\\.bic\\s*:=\\s*'(.+?)'";
        return Utils.match(instance, bicPattern);
    }

    private List<String> getCountries(String instance) {
        String countryPattern = "!\\w+\\.country\\s*:=\\s*'(.+?)'";
        return Utils.match(instance, countryPattern);
    }

    /*
     * A BIC/IBAN is valid in lenient mode, if it is syntactically correct.
     * It is valid in strict mode, if a BankService implementation exists, which knows the BIC.
     * It is valid in pragmatic mode, when it is valid in strict mode with an existing BankService implementation or valid in lenient mode without an existing BankService implementation.
     */
    // Empty methods for annotation extraction
    void leinentBIC(@BIC(check = Check.LENIENT) String bic) {}
    void leinentIBAN(@IBAN(check = Check.LENIENT) String iban) {}

    private int[] validateIbans(List<String> ibans) {
        int[] valid = {0,0};
        IbanValidator ibanValidator = new IbanValidator();
        try {
            ibanValidator.initialize((IBAN) getClass().getDeclaredMethod("leinentIBAN", String.class).getParameterAnnotations()[0][0]);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (String iban : ibans) {
            // Remove spaces, hyphens, dots and convert to uppercase
            iban = iban.replaceAll("[\\s\\-.]", "").toUpperCase().trim();
            if (ibanValidator.isValid(iban, null)){
                valid[0]++;
            } else {
                failedRealIbans.add(iban);
            } 
            
            if (Utils.validMatch(iban, "([A-Z]{2})(\\d{2})([A-Z0-9]{11,30})")) {
                valid[1]++;  
            } else {
                failedIbans.add(iban);
            }

        }

        return valid;
    }

    private int validateBics(List<String> bics) {
        int valid = 0;
        BicValidator bicValidator = new BicValidator();
        try {
            bicValidator.initialize((BIC)getClass().getDeclaredMethod("leinentBIC", String.class).getParameterAnnotations()[0][0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String bic : bics) {
            bic = bic.replaceAll("[\\s\\-.]", "").toUpperCase().trim();
            if (bicValidator.isValid(bic, null)) {
                valid++;
            } else {
                failedBics.add(bic);
            }
        }

        return valid;
    }

    private int validateCountries(List<String> countries) {
        int valid = 0;
        for (String country : countries) {
            if (Utilities.isValidCountryName(country)) {
                valid++;
            } else {
                failedCountries.add(country);
            }
        }

        return valid;
    }

    private void calculateIbans(String diagram, String instance) {
        // Implementation that updates validIbans and totalIbans
        List<String> ibans = getIbans(instance);
        this.realIbans = validateIbans(ibans)[0];
        this.validIbans = validateIbans(ibans)[1];
        this.totalIbans = ibans.size();
    }
    
    private void calculateBics(String diagram, String instance) {
        // Implementation that updates validBics and totalBics
        List<String> bics = getBics(instance);
        this.validBics = validateBics(bics);
        this.totalBics = bics.size();
    }
    
    private void calculateCountries(String diagram, String instance) {
        // Implementation that updates validCountries and totalCountries
        List<String> countries = getCountries(instance);
        this.validCountries = validateCountries(countries);
        this.totalCountries = countries.size();
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        System.out.println(this.getClass().getSimpleName() + " calculating ALL metrics for: " + instancePath);
        String diagram = Utils.readFile(diagramPath);
        String instance = Utils.readFile(instancePath);
        
        calculateIbans(diagram, instance);
        calculateBics(diagram, instance);
        calculateCountries(diagram, instance);
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof Bank)) {
            return;
        }
        
        Bank other = (Bank) otherMetrics;
        this.realIbans += other.realIbans;
        this.validIbans += other.validIbans;
        this.validBics += other.validBics;
        this.validCountries += other.validCountries;
        this.totalIbans += other.totalIbans;
        this.totalBics += other.totalBics;
        this.totalCountries += other.totalCountries;

        this.failedRealIbans.addAll(other.failedRealIbans);
        this.failedBics.addAll(other.failedBics);
        this.failedCountries.addAll(other.failedCountries);
        this.failedIbans.addAll(other.failedIbans);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Bank | Invalid | Total | Failure (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("IBANs (Real)", totalIbans - realIbans, totalIbans))
          .append(Utilities.formatMetricRow("IBANs (Realistic)", totalIbans - validIbans, totalIbans))
          .append(Utilities.formatMetricRow("BICs (Realistic)", totalBics - validBics, totalBics))
          .append(Utilities.formatMetricRow("Countries (Realistic)", totalCountries - validCountries, totalCountries));

        sb.append(Utilities.getStringList("Failed IBANs (Real)", failedRealIbans));
        sb.append(Utilities.getStringList("Failed IBANs (Realistic)", failedIbans));
        sb.append(Utilities.getStringList("Failed BICs (Realistic)", failedBics));
        sb.append(Utilities.getStringList("Failed Countries (Realistic)", failedCountries));
        
        return sb.toString();
    }
    
}
