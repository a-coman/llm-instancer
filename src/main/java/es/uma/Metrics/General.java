package es.uma.Metrics;

import java.util.ArrayList;

import es.uma.Use;
import es.uma.Utils;

public class General implements IMetrics {
    
    private int syntaxErrors, multiplicitiesErrors, invariantsErrors;
    private ArrayList<String> syntaxErrorsList, multiplicitiesErrorsList, invariantsErrorsList;
    
    // For invalid category increments (just for InvalidToString)
    private int invalidMultiplicitiesErrors, invalidInvariantsErrors;
    private ArrayList<String> invalidMultiplicitiesList, invalidInvariantsList;

    public General() {
        syntaxErrors = 0;
        multiplicitiesErrors = 0;
        invariantsErrors = 0;
        syntaxErrorsList = new ArrayList<>();
        multiplicitiesErrorsList = new ArrayList<>();
        invariantsErrorsList = new ArrayList<>();

        // For invalid category increments (just for InvalidToString)
        invalidMultiplicitiesErrors = 0;
        invalidInvariantsErrors = 0;
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

        // For invalid category increments (just for InvalidToString)
        this.invalidMultiplicitiesErrors += other.invalidMultiplicitiesErrors;
        this.invalidInvariantsErrors += other.invalidInvariantsErrors;
        this.invalidMultiplicitiesList.addAll(other.invalidMultiplicitiesList);
        this.invalidInvariantsList.addAll(other.invalidInvariantsList);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| General | Value | \n");
        sb.append("|---|---| \n");
        sb.append("| Syntax Errors | ").append(syntaxErrors).append(" | \n");
        sb.append("| Multiplicities Errors | ").append(multiplicitiesErrors).append(" | \n");
        sb.append("| Invariants Errors | ").append(invariantsErrors).append(" | \n");

        sb.append(Utilities.getStringList("Syntax Errors", syntaxErrorsList));
        sb.append(Utilities.getStringList("Multiplicities Errors", multiplicitiesErrorsList));
        sb.append(Utilities.getStringList("Invariants Errors", invariantsErrorsList));

        return sb.toString();
    }

    public String invalidToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| General | Value | \n");
        sb.append("|---|---| \n");
        sb.append("| Syntax Errors | ").append(syntaxErrors).append(" | \n");
        sb.append(Utilities.getStringList("Syntax Errors", syntaxErrorsList));

        sb.append("\n");
        sb.append("| [Overconstraints Detection] General | Value | \n");
        sb.append("|---|---| \n");
        sb.append("| Multiplicities Errors | ").append(invalidMultiplicitiesErrors).append(" | \n");
        sb.append("| Invariants Errors | ").append(invalidInvariantsErrors).append(" | \n");

        sb.append(Utilities.getStringList("[Overconstraints Detection] Multiplicities Errors", invalidMultiplicitiesList));
        sb.append(Utilities.getStringList("[Overconstraints Detection] Invariants Errors", invalidInvariantsList));

        return sb.toString();
    }

    // Main for testing purposes
    public static void main(String[] args) {
        String testDiagram = "./src/main/resources/prompts/bank/diagram.use";
        String testInstance = "./src/main/resources/instances/CoT/bank/GPT_4O/11-03-2025--18-41-54/gen2/outputValid.soil";

        General general = new General();
        general.calculate(testDiagram, testInstance);
        System.out.println(general.toString());
    }
    
}
