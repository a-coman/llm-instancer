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
        String[] syntaxErrors = syntax.equals("OK") ? new String[0] : syntax.split("\n");
        this.syntaxErrors += syntaxErrors.length;
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
        ArrayList<String> invariantErrors = Utils.split(invariants, "(?m)^checking invariant.*?(FAILED|N/A)\\.\\s*$(.*\\n)*?(?=^checking invariant|\\z)");
        this.invariantsErrors += invariantErrors.size();
        use.close();
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        calculateInvalid(diagramPath, instancePath);
        calculateInvariantsErrors(diagramPath, instancePath);
    }

    @Override
    public void calculateInvalid(String diagramPath, String instancePath) {
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
        sb.append("Syntax Errors: ").append(syntaxErrors).append("\n");
        sb.append("Multiplicities Errors: ").append(multiplicitiesErrors).append("\n");
        sb.append("Invariants Errors: ").append(invariantsErrors).append("\n");
        return sb.toString();
    }
}
