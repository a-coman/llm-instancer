package es.uma.Metrics;

import java.util.ArrayList;

import es.uma.Use;
import es.uma.Utils;
import es.uma.CoT.CategoryPrompts;

public class General extends Metric {

    private static int sumOfSyntaxErrors = 0;
    private static int sumOfMultiplicitiesErrors = 0;
    private static int sumOfInvariantErrors = 0;

    private static void append(StringBuilder metrics, String text) {
        metrics.append("```\n");
        metrics.append(text);
        metrics.append("\n```\n");
    }

    private static void append(StringBuilder metrics, ArrayList<String> list) {
        for (String error : list) {
            metrics.append("```\n");
            metrics.append(error);
            metrics.append("\n```\n");
        }
        metrics.append("\n");
    }

    private static void append(StringBuilder metrics, String[] list) {
        for (String error : list) {
            metrics.append(error);
            metrics.append("\n");
        }
        metrics.append("\n");
    }

    private static int addSyntax(Use use, StringBuilder metrics, String diagramPath, String instancePath) { 
        String syntax = use.checkSyntax(diagramPath, instancePath);
        String[] syntaxErrors = syntax.equals("OK") ? new String[0] : syntax.split("\n");
        sumOfSyntaxErrors += syntaxErrors.length;
        metrics.append("| Syntax errors: |\n|---|\n");
        append(metrics, syntaxErrors);
        return syntaxErrors.length;
        
    }

    private static int addMultiplicities(Use use, StringBuilder metrics, String diagramPath, String instancePath) { 
        String multiplicities = use.checkMultiplicities(diagramPath, instancePath);
        ArrayList<String> multiplicitiesErrors = Utils.split(multiplicities, "Multiplicity constraint violation[\\s\\S]*?(?=Multiplicity constraint violation|$)");
        sumOfMultiplicitiesErrors += multiplicitiesErrors.size();
        metrics.append("| Multiplicities errors: |\n|---|\n");
        append(metrics, multiplicitiesErrors);
        return multiplicitiesErrors.size();
    }

    private static int addInvariants(Use use, StringBuilder metrics, String diagramPath, String instancePath) { 
        String invariants = use.checkInvariants(diagramPath, instancePath, "");
        ArrayList<String> invariantErrors = Utils.split(invariants, "(?m)^checking invariant.*?(FAILED|N/A)\\.\\s*$(.*\\n)*?(?=^checking invariant|\\z)");
        sumOfInvariantErrors += invariantErrors.size();
        metrics.append("| Invariants errors: |\n|---|\n");
        append(metrics,invariantErrors);
        return invariantErrors.size();
    }

    @Override
    public String getSimpleMetrics(String diagramPath, String genPath) {
        StringBuilder metrics = new StringBuilder();
        String instance = Utils.readFile(genPath + "output.soil");
        metrics.append("## Instance\n");
        append(metrics, instance);

        Use use = new Use();
        addSyntax(use, metrics, diagramPath, genPath);
        addMultiplicities(use, metrics, diagramPath, genPath);
        addInvariants(use, metrics, diagramPath, genPath);
        use.close();

        return metrics.toString();

    }

    private static void addSummary(StringBuilder metrics, int syntaxErrors, int multiplicitiesErrors, int invariantErrors) {
        metrics.append("| Metric | Value |\n");
        metrics.append("| --- | --- |\n");
        metrics.append("| Sum of syntax errors | " + syntaxErrors + " |\n");
        metrics.append("| Sum of multiplicities errors | " + multiplicitiesErrors + " |\n");
        metrics.append("| Sum of invariant errors | " + invariantErrors + " |\n");
    }

    @Override
    public String getCoTMetrics(String diagramPath, String genPath) {
        StringBuilder metrics = new StringBuilder();
        CategoryPrompts categoryPrompts = new CategoryPrompts();
        Use use = new Use();

        int genSyntax = 0, genMultiplicities = 0, genInvariants = 0;

        for (String categoryId : categoryPrompts.list.keySet()) {
            String instancePath = genPath + categoryId + ".soil";
            String instance = Utils.readFile(instancePath);
            metrics.append("\n ## Instance " + categoryId + "\n");
            append(metrics, instance);
            
            int syntax = addSyntax(use, metrics, diagramPath, instancePath);
            int multiplicities = addMultiplicities(use, metrics, diagramPath, instancePath);
            int invariants = addInvariants(use, metrics, diagramPath, instancePath);
            
            genSyntax += syntax;
            genMultiplicities += multiplicities;
            genInvariants += invariants;

            metrics.append("\n ## Summary\n");
            addSummary(metrics, syntax, multiplicities, invariants);
        }

        metrics.append("\n # Generation summary\n");
        addSummary(metrics, genSyntax, genMultiplicities, genInvariants);
        use.close();
        return metrics.toString();

    }

    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n # Generations summary\n");
        sb.append("| Metric | Value |\n");
        sb.append("| --- | --- |\n");
        sb.append("| Sum of syntax errors | " + sumOfSyntaxErrors + " |\n");
        sb.append("| Sum of multiplicities errors | " + sumOfMultiplicitiesErrors + " |\n");
        sb.append("| Sum of invariant errors | " + sumOfInvariantErrors + " |\n");
        return sb.toString();
    }
    
}
