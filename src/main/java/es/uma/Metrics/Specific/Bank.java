package es.uma.Metrics.Specific;

import java.util.ArrayList;
import java.util.List;

import de.schegge.bank.validator.BIC;
import de.schegge.bank.validator.BicValidator;
import de.schegge.bank.validator.IBAN;
import de.schegge.bank.validator.IbanValidator;
import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class Bank implements IMetrics {

    private int validIbans, validBics, validCountries;
    private int totalIbans, totalBics, totalCountries;
    private ArrayList<String> failedIbans, failedBics, failedCountries;

    public Bank() {
        validIbans = 0;
        validBics = 0;
        validCountries = 0;
        totalIbans = 0;
        totalBics = 0;
        totalCountries = 0;

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
    void pragmaticBIC(@BIC String bic) {}
    void pragmaticIBAN(@IBAN String iban) {}

    private int validateIbans(List<String> ibans) {
        int valid = 0;
        IbanValidator ibanValidator = new IbanValidator();
        try {
            ibanValidator.initialize((IBAN) getClass().getDeclaredMethod("pragmaticIBAN", String.class).getParameterAnnotations()[0][0]);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (String iban : ibans) {
            if (ibanValidator.isValid(iban, null)){
                valid++;
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
            bicValidator.initialize((BIC)getClass().getDeclaredMethod("pragmaticBIC", String.class).getParameterAnnotations()[0][0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String bic : bics) {
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
        this.validIbans = validateIbans(ibans);
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
    public void calculateInvalid(String diagramPath, String instancePath) {
        // For Bank all calculations are always performed for invalid
        calculate(diagramPath, instancePath);
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof Bank)) {
            return;
        }
        
        Bank other = (Bank) otherMetrics;
        this.validIbans += other.validIbans;
        this.validBics += other.validBics;
        this.validCountries += other.validCountries;
        this.totalIbans += other.totalIbans;
        this.totalBics += other.totalBics;
        this.totalCountries += other.totalCountries;

        this.failedBics.addAll(other.failedBics);
        this.failedCountries.addAll(other.failedCountries);
        this.failedIbans.addAll(other.failedIbans);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Bank | Valid | Total | Success (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("IBANs", validIbans, totalIbans))
          .append(Utilities.formatMetricRow("BICs", validBics, totalBics))
          .append(Utilities.formatMetricRow("Countries", validCountries, totalCountries));

        sb.append(Utilities.getStringList("Failed IBANs", failedIbans));
        sb.append(Utilities.getStringList("Failed BICs", failedBics));
        sb.append(Utilities.getStringList("Failed Countries", failedCountries));
        
        return sb.toString();
    }
    
}
