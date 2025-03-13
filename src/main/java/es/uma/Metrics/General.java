package es.uma.Metrics;

import java.util.ArrayList;

import es.uma.Use;
import es.uma.Utils;

public class General implements IMetrics {
    
    private int syntaxErrors, multiplicitiesErrors, invariantsErrors;

    public General() {
        syntaxErrors = 0;
        multiplicitiesErrors = 0;
        invariantsErrors = 0;
    }

    private void calculateSyntaxErrors(String diagramPath, String instancePath) {
        Use use = new Use();
        String syntax = use.checkSyntax(diagramPath, instancePath);
        ArrayList<String> syntaxErrors = syntax.equals("OK") ? new ArrayList<>() : Utils.split(syntax, "(<input>:.*?\\n|Error:.*?\\n|Warning:.*?\\n)");
        this.syntaxErrors += syntaxErrors.size();
        use.close();
    }

    private void calculateMultiplicitiesErrors(String diagramPath, String instancePath) {   
        Use use = new Use();
        String multiplicities = use.checkMultiplicities(diagramPath, instancePath);
        ArrayList<String> multiplicitiesErrors = Utils.split(multiplicities, "Multiplicity constraint violation[\\s\\S]*?(?=Multiplicity constraint violation|$)");
        this.multiplicitiesErrors += multiplicitiesErrors.size();
        use.close();
    }

    private void calculateInvariantsErrors(String diagramPath, String instancePath) {   
        Use use = new Use();
        String invariants = use.checkInvariants(diagramPath, instancePath, "");
        // REGEX to match also N/A -> "(?m)^checking invariant.*?(FAILED|N/A)\\.?\\s*$"
        ArrayList<String> invariantErrors = Utils.split(invariants, "(?m)^checking invariant.*FAILED\\.?\\s*$");
        this.invariantsErrors += invariantErrors.size();
        use.close();
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        System.out.println(this.getClass().getSimpleName() + " calculating ALL metrics for: " + instancePath);
        calculateInvalid(diagramPath, instancePath);
        calculateInvariantsErrors(diagramPath, instancePath);
    }

    @Override
    public void calculateInvalid(String diagramPath, String instancePath) {
        System.out.println(this.getClass().getSimpleName() + " calculating Invalid metrics for: " + instancePath);
        calculateSyntaxErrors(diagramPath, instancePath);
        calculateMultiplicitiesErrors(diagramPath, instancePath);
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
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| General | Value | \n");
        sb.append("|---|---| \n");
        sb.append("| Syntax Errors | ").append(syntaxErrors).append(" | \n");
        sb.append("| Multiplicities Errors | ").append(multiplicitiesErrors).append(" | \n");
        sb.append("| Invariants Errors | ").append(invariantsErrors).append(" | \n");
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
