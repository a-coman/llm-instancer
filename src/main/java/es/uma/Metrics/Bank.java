package es.uma.Metrics;

import java.util.Arrays;
import java.util.List;

import de.schegge.bank.validator.BIC;
import de.schegge.bank.validator.BicValidator;
import de.schegge.bank.validator.IBAN;
import de.schegge.bank.validator.IbanValidator;
import es.uma.Utils;

public class Bank extends Metric {

    private static int sumOfValidIbans = 0;
    private static int sumOfValidCountries = 0;
    private static int sumOfValidBics = 0;
    private static int sumOfTotalIbans = 0;
    private static int sumOfTotalCountries = 0;
    private static int sumOfTotalBics = 0;

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
            if (ibanValidator.isValid(iban, null))
                valid++;

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
            }
        }

        return valid;
    }

    private int validateCountries(List<String> countries) {
        int valid = 0;
        for (String country : countries) {
            if (Utilities.isValidCountryName(country)) {
                valid++;
            }
        }

        return valid;
    }

    @Override
    public String getSimpleMetrics(String diagramPath, String genPath) {
        StringBuilder metrics = new StringBuilder();
        String instance = Utils.readFile(genPath + "output.soil");

        List<String> ibans = getIbans(instance);
        List<String> bics = getBics(instance);
        List<String> countries = getCountries(instance);
        sumOfTotalIbans += ibans.size();
        sumOfTotalBics += bics.size();
        sumOfTotalCountries += countries.size();

        int validIbans = validateIbans(ibans);
        int validBics = validateBics(bics);
        int validCountries = validateCountries(countries);
        sumOfValidIbans += validIbans;
        sumOfValidBics += validBics;
        sumOfValidCountries += validCountries;

        metrics.append("| Bank-Specific | Valid | Total | (%) Success | \n");
        metrics.append("| --- | --- | --- | --- | \n");
        metrics.append("| IBAN | " + validIbans + " | " + ibans.size() + " | " + (validIbans * 100.0 / ibans.size()) + "% | \n");
        metrics.append("| BIC | " + validBics + " | " + bics.size() + " | " + (validBics * 100.0 / bics.size()) + "% | \n");
        metrics.append("| Countries | " + validCountries + " | " + countries.size() + " | " + (validCountries * 100.0 / countries.size()) + "% | \n");

        return metrics.toString();
    }

    @Override
    public String getCoTMetrics(String diagramPath, String genPath) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCoTMetrics'");
    }

    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();

        sb.append("| Bank-Specific | Valid | Total | (%) Success | \n");
        sb.append("| --- | --- | --- | --- | \n");
        sb.append("| IBAN | " + sumOfValidIbans + " | " + sumOfTotalIbans + " | " + (sumOfValidIbans * 100.0 / sumOfTotalIbans) + "% | \n");
        sb.append("| BIC | " + sumOfValidBics + " | " + sumOfTotalBics + " | " + (sumOfValidBics * 100.0 / sumOfTotalBics) + "% | \n");
        sb.append("| Countries | " + sumOfValidCountries + " | " + sumOfTotalCountries + " | " + (sumOfValidCountries * 100.0 / sumOfTotalCountries) + "% | \n");

        return sb.toString();
    }

    public static void main(String[] args) {
        Bank bank = new Bank();
        
        String inputTest = """
        !new Bank('bank1')
        !bank1.country := 'Spain'
        !bank1.name := 'Banco Santander'
        !bank1.bic := 'BSCHESMMXXX'

        !new Account('account1')
        !account1.iban := 'ES6000491500051234567892'
        !account1.balance := 1000

        !new Person('person1')
        !person1.age := 25
        !person1.firstName := 'Alice'
        !person1.lastName := 'Smith'

        !insert (person1, account1) into Ownership
        !insert (person1, account1) into Use
        !insert (bank1, account1) into AccountOfBanks

        !new Account('account2')
        !account2.iban := 'ES6000491500059876543210'
        !account2.balance := 5000

        !insert (person1, account2) into Use
        !insert (bank1, account2) into AccountOfBanks

        !new Bank('bank2')
        !bank2.country := 'Germany'
        !bank2.name := 'Deutsche Bank'
        !bank2.bic := 'DEUTDEFFXXX'

        !new Account('account3')
        !account3.iban := 'DE89370400440532013001'
        !account3.balance := 2000

        !new Person('person2')
        !person2.age := 30
        !person2.firstName := 'Bob'
        !person2.lastName := 'Johnson'

        !insert (person2, account3) into Ownership
        !insert (person2, account3) into Use
        !insert (bank2, account3) into AccountOfBanks

        !new Account('account4')
        !account4.iban := 'DE89370400440532013002'
        !account4.balance := 7500

        !insert (person2, account4) into Use
        !insert (bank2, account4) into AccountOfBanks

        !new Bank('bank3')
        !bank3.country := 'France'
        !bank3.name := 'BNP Paribas'
        !bank3.bic := 'BNPAFRPPXXX'

        !new Account('account5')
        !account5.iban := 'FR7630004000011234567890136'
        !account5.balance := 3000

        !new Person('person3')
        !person3.age := 40
        !person3.firstName := 'Charlie'
        !person3.lastName := 'Brown'

        !insert (person3, account5) into Ownership
        !insert (person3, account5) into Use
        !insert (bank3, account5) into AccountOfBanks
        """;

        
        System.out.println("IBANS: " + bank.getIbans(inputTest).toString());
        System.out.println("BICS: " + bank.getBics(inputTest).toString());
        System.out.println("COUNTRIES: " + bank.getCountries(inputTest).toString());
        
        int iban = bank.validateIbans(Arrays.asList("DE89370400440532013000", "54665465654", "DE89370400440532013000")); // 2 Valid IBAN
        System.out.println("Iban result: " + iban);

        int bic = bank.validateBics(Arrays.asList("UBSWCHZH80A", "BOTKJPJT")); // 1 Valid BIC
        System.out.println("Bic result: " + bic);

        int country = bank.validateCountries(Arrays.asList("España", "Spain", "United States", "Spain", "asdasdas", "UK")); // 4 Valid Countries
        System.out.println("Country result: " + country);

    }


}
