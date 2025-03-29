package es.uma.Metrics;

import java.util.ArrayList;

import es.uma.Use;
import es.uma.Utils;

public class General implements IMetrics {
    
    private int syntaxErrors, multiplicitiesErrors, invariantsErrors, totalSyntax, totalMultiplicities, totalInvariants;
    private ArrayList<String> syntaxErrorsList, multiplicitiesErrorsList, invariantsErrorsList;
    
    // For invalid category increments (just for InvalidToString)
    private int invalidMultiplicitiesErrors, invalidInvariantsErrors, totalInvalidMultiplicities, totalInvalidInvariants;
    private ArrayList<String> invalidMultiplicitiesList, invalidInvariantsList;

    public General() {
        syntaxErrors = 0;
        multiplicitiesErrors = 0;
        invariantsErrors = 0;
        totalSyntax = 0;
        totalMultiplicities = 0;
        totalInvariants = 0;
        syntaxErrorsList = new ArrayList<>();
        multiplicitiesErrorsList = new ArrayList<>();
        invariantsErrorsList = new ArrayList<>();

        // For invalid category increments (just for InvalidToString)
        invalidMultiplicitiesErrors = 0;
        invalidInvariantsErrors = 0;
        totalInvalidMultiplicities = 0;
        totalInvalidInvariants = 0;
        invalidMultiplicitiesList = new ArrayList<>();
        invalidInvariantsList = new ArrayList<>();
    }

    private ArrayList<String> getSyntaxErrors(String diagramPath, String instancePath) {
        Use use = new Use();
        String syntax = use.checkSyntax(diagramPath, instancePath);
        ArrayList<String> syntaxErrors = syntax.equals("OK") ? new ArrayList<>() : Utils.split(syntax, "(<input>:.*?\\n|Error:.*?\\n|Warning:.*?\\n|INTERNAL ERROR:.*?\\n)");
        use.close();
        return syntaxErrors;
    }

    private ArrayList<String> getMultiplicitiesErrors(String diagramPath, String instancePath) {   
        Use use = new Use();
        String multiplicities = use.checkMultiplicities(diagramPath, instancePath);
        ArrayList<String> multiplicitiesErrors = Utils.split(multiplicities, "Multiplicity constraint violation[\\s\\S]*?(?=Multiplicity constraint violation|$)");
        use.close();
        return multiplicitiesErrors;
    }

    private ArrayList<String> getInvariantsErrors(String diagramPath, String instancePath) {   
        Use use = new Use();
        String invariants = use.checkInvariants(diagramPath, instancePath, "");
        // REGEX to match also N/A -> "(?m)^checking invariant.*?(FAILED|N/A)\\.?\\s*$"
        ArrayList<String> invariantErrors = Utils.split(invariants, "(?m)^checking invariant.*FAILED\\.?\\s*$");
        use.close();
        return invariantErrors;
    }

    private int getTotalSyntax(String instancePath) {
        String instance = Utils.readFile(instancePath);
        int count = 0;
    
        // Search for "!new"
        int index = 0;
        while ((index = instance.indexOf("!new", index)) != -1) {
            count++;
            index += 4;
        }
        
        // Search for "!insert"
        index = 0;
        while ((index = instance.indexOf("!insert", index)) != -1) {
            count++;
            index += 7;
        }
        
        // Search for ":="
        index = 0;
        while ((index = instance.indexOf(":=", index)) != -1) {
            count++;
            index += 2;
        }
        
        return count;
    }

    private int getTotalMultiplicities(String instancePath) {
        String instance = Utils.readFile(instancePath);
        String pattern = "insert\\s+(.+?)\\s+into";
        return Utils.split(instance, pattern).size();
    }

    private int getTotalInvariants(String diagramPath) {
        String diagram = Utils.readFile(diagramPath);
        String pattern = "(context)\\s+\\S+\\s+inv";
        return Utils.split(diagram, pattern).size();
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        System.out.println(this.getClass().getSimpleName() + " calculating ALL metrics for: " + instancePath);
        ArrayList<String> parsedSyntaxErrors = getSyntaxErrors(diagramPath, instancePath);
        ArrayList<String> parsedMultiplicitiesErrors = getMultiplicitiesErrors(diagramPath, instancePath);
        ArrayList<String> parsedInvariantsErrors =  getInvariantsErrors(diagramPath, instancePath);
        
        syntaxErrors += parsedSyntaxErrors.size();
        multiplicitiesErrors += parsedMultiplicitiesErrors.size();
        invariantsErrors += parsedInvariantsErrors.size();

        syntaxErrorsList.addAll(parsedSyntaxErrors);
        multiplicitiesErrorsList.addAll(parsedMultiplicitiesErrors);
        invariantsErrorsList.addAll(parsedInvariantsErrors);

        totalSyntax += getTotalSyntax(instancePath);
        totalMultiplicities += getTotalMultiplicities(instancePath);
        totalInvariants += getTotalInvariants(diagramPath);

    }

    public void calculateInvalid(String diagramPath, String instancePath) {
        System.out.println(this.getClass().getSimpleName() + " calculating Invalid metrics for: " + instancePath);
        // General syntax errors
        ArrayList<String> parsedSyntaxErrors = getSyntaxErrors(diagramPath, instancePath);
        syntaxErrors += parsedSyntaxErrors.size();
        syntaxErrorsList.addAll(parsedSyntaxErrors);

        // For invalid category increments (just for InvalidToString)
        ArrayList<String> parsedMultiplicitiesErrors = getMultiplicitiesErrors(diagramPath, instancePath);
        ArrayList<String> parsedInvariantsErrors =  getInvariantsErrors(diagramPath, instancePath);
        invalidMultiplicitiesErrors += parsedMultiplicitiesErrors.size();
        invalidInvariantsErrors += parsedInvariantsErrors.size();
        invalidMultiplicitiesList.addAll(parsedMultiplicitiesErrors);
        invalidInvariantsList.addAll(parsedInvariantsErrors);

        totalSyntax += getTotalSyntax(instancePath);
        totalInvalidMultiplicities += getTotalMultiplicities(instancePath);
        totalInvalidInvariants += getTotalInvariants(diagramPath);
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof General)) {
            return;
        }
        
        General other = (General) otherMetrics;
        this.syntaxErrors += other.syntaxErrors;
        this.multiplicitiesErrors += other.multiplicitiesErrors;
        this.invariantsErrors += other.invariantsErrors;

        this.syntaxErrorsList.addAll(other.syntaxErrorsList);
        this.multiplicitiesErrorsList.addAll(other.multiplicitiesErrorsList);
        this.invariantsErrorsList.addAll(other.invariantsErrorsList);

        this.totalSyntax += other.totalSyntax;
        this.totalMultiplicities += other.totalMultiplicities;
        this.totalInvariants += other.totalInvariants;

        // For invalid category increments (just for InvalidToString)
        this.invalidMultiplicitiesErrors += other.invalidMultiplicitiesErrors;
        this.invalidInvariantsErrors += other.invalidInvariantsErrors;
        this.totalInvalidMultiplicities += other.totalInvalidMultiplicities;
        this.totalInvalidInvariants += other.totalInvalidInvariants;
        this.invalidMultiplicitiesList.addAll(other.invalidMultiplicitiesList);
        this.invalidInvariantsList.addAll(other.invalidInvariantsList);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| General | Errors | Total | Failure (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Syntax Errors", syntaxErrors, totalSyntax));
        sb.append(Utilities.formatMetricRow("Multiplicities Errors", multiplicitiesErrors, totalMultiplicities));
        sb.append(Utilities.formatMetricRow("Invariants Errors", invariantsErrors, totalInvariants));
        
        sb.append(Utilities.getStringList("Syntax Errors", syntaxErrorsList));
        sb.append(Utilities.getStringList("Multiplicities Errors", multiplicitiesErrorsList));
        sb.append(Utilities.getStringList("Invariants Errors", invariantsErrorsList));

        return sb.toString();
    }

    public String invalidToString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("| [Overconstraints Detection] | Errors | Total | Failure (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Multiplicities Errors (Not included on General)", invalidMultiplicitiesErrors, totalInvalidMultiplicities));
        sb.append(Utilities.formatMetricRow("Invariants Errors (Not included on General)", invalidInvariantsErrors, totalInvalidInvariants));

        sb.append(Utilities.getStringList("[Overconstraints Detection] Multiplicities Errors (Not included on General)", invalidMultiplicitiesList));
        sb.append(Utilities.getStringList("[Overconstraints Detection] Invariants Errors (Not included on General)", invalidInvariantsList));

        return sb.toString();
    }

    // Main for testing purposes
    public static void main(String[] args) {
        String testDiagram = "./src/main/resources/prompts/bank/diagram.use";
        String testInstance = "./src/main/resources/instances/CoT/bank/GPT_4O/11-03-2025--18-41-54/gen2/outputValid.soil";

        General general = new General();
        general.calculate(testDiagram, testInstance);
        System.out.println(general.toString());
        System.out.println(general.invalidToString());
    }
    
}
