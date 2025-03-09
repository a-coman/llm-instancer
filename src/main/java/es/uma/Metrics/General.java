package es.uma.Metrics;

import java.util.ArrayList;

import es.uma.Use;
import es.uma.Utils;

public class General {
    
    private int sumOfSyntaxErrors;
    private int sumOfMultiplicitiesErrors;
    private int sumOfInvariantErrors;
    public int getSumOfSyntaxErrors() {
        return sumOfSyntaxErrors;
    }

    public int getSumOfMultiplicitiesErrors() {
        return sumOfMultiplicitiesErrors;
    }

    public int getSumOfInvariantErrors() {
        return sumOfInvariantErrors;
    }

    private StringBuilder metrics;

    private void append(ArrayList<String> list) {
        for (String error : list) {
            metrics.append("```\n");
            metrics.append(error);
            metrics.append("\n```\n");
        }
        metrics.append("\n");
    }

    private void append(String[] list) {
        for (String error : list) {
            metrics.append(error);
            metrics.append("\n");
        }
        metrics.append("\n");
    }

    public General() {
        sumOfSyntaxErrors = 0;
        sumOfMultiplicitiesErrors = 0;
        sumOfInvariantErrors = 0;
        metrics = new StringBuilder();
    }

    public void calculateIntanceMetrics(String diagramPath, String genPath, String categoryId) {
        String instancePath = genPath + categoryId + ".soil";
        String instance = Utils.readFile(instancePath);

        metrics.append("\n ## Instance " + categoryId + "\n");
        metrics.append("```\n");
        metrics.append(instance);
        metrics.append("\n```\n");

        Use use = new Use();

        // Check syntax
        String syntax = use.checkSyntax(diagramPath, instancePath);
        String[] syntaxErrors = syntax.equals("OK") ? new String[0] : syntax.split("\n");
        sumOfSyntaxErrors += syntaxErrors.length;
        metrics.append("| Syntax errors: |\n|---|\n");
        append(syntaxErrors);
        
        // Check multiplicities
        String multiplicities = use.checkMultiplicities(diagramPath, instancePath);
        ArrayList<String> multiplicitiesErrors = Utils.split(multiplicities, "Multiplicity constraint violation[\\s\\S]*?(?=Multiplicity constraint violation|$)");
        sumOfMultiplicitiesErrors += multiplicitiesErrors.size();
        metrics.append("| Multiplicities errors: |\n|---|\n");
        append(multiplicitiesErrors);

        // Check invariants
        String invariants = use.checkInvariants(diagramPath, instancePath, "");
        ArrayList<String> invariantErrors = Utils.split(invariants, "(?m)^checking invariant.*?(FAILED|N/A)\\.\\s*$(.*\\n)*?(?=^checking invariant|\\z)");
        sumOfInvariantErrors += invariantErrors.size();
        metrics.append("| Invariants errors: |\n|---|\n");
        append(invariantErrors);

        use.close();
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n ## Summary\n");
        sb.append("| Metric | Value |\n");
        sb.append("| --- | --- |\n");
        sb.append("| Sum of syntax errors | " + sumOfSyntaxErrors + " |\n");
        sb.append("| Sum of multiplicities errors | " + sumOfMultiplicitiesErrors + " |\n");
        sb.append("| Sum of invariant errors | " + sumOfInvariantErrors + " |\n");
        return sb.toString();
    }

    public String getIntanceMetrics() {
        return metrics.toString();
    }

}
