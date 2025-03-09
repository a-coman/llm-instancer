package es.uma.Metrics;

import es.uma.Utils;

import java.util.ArrayList;

import es.uma.Experiment;
import es.uma.CoT.CategoryPrompts;

public class Metrics {

    private static String getSummary(ArrayList<General> generalMetricsList) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Generations Summary \n");
        sb.append("| Metric | Value |\n");
        sb.append("| --- | --- |\n");
        int syntaxErrors = 0, multiplicitiesErrors = 0, invariantErrors = 0;
        for (General general : generalMetricsList) {
            syntaxErrors += general.getSumOfSyntaxErrors();
            multiplicitiesErrors += general.getSumOfMultiplicitiesErrors();
            invariantErrors += general.getSumOfInvariantErrors();
        }
        sb.append("| Sum of syntax errors | " + syntaxErrors+ " |\n");
        sb.append("| Sum of multiplicities errors | " + multiplicitiesErrors + " |\n");
        sb.append("| Sum of invariant errors | " + invariantErrors + " |\n");
        return sb.toString();
    }

    public static void logMetrics(Experiment experiment) {
        
        String diagramPath = experiment.umlPath;
        CategoryPrompts categoryPrompts = new CategoryPrompts();
        ArrayList<General> generalMetricsList = new ArrayList<>();

        for (int i = 1; i <= experiment.repetitions; i++) {
            String genPath = experiment.instancePath + "gen" + i + "/";
            General generalMetrics = new General();
            
            for (String categoryId : categoryPrompts.list.keySet()) {
                generalMetrics.calculateIntanceMetrics(diagramPath, genPath, categoryId);
                //Specific.calculateMetrics(diagramPath, path, experiment.system);
            }
            
            Utils.saveFile("# Generation " + i + " \n", genPath, "metrics.md");
            Utils.saveFile(generalMetrics.getIntanceMetrics(), genPath, "metrics.md");
            Utils.saveFile(generalMetrics.getSummary(), genPath, "metrics.md");
            generalMetricsList.add(generalMetrics);

            //Specific.saveMetrics(diagramPath, instancePath, experiment.system);
        }
        
        Utils.saveFile(getSummary(generalMetricsList), experiment.instancePath, "metrics.md");
        //Utils.saveFile(General.getSummary(), experiment.instancePath, "metrics.md");
        //Specific.saveMetrics(diagramPath, experiment.instancePath, experiment.system);
                

    }
}
