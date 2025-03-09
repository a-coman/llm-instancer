package es.uma.Metrics;

import es.uma.Utils;

public class Bank extends Specific {
    private static StringBuilder metrics = new StringBuilder();
    private static int totalBanks = 0;
    private static int totalAccounts = 0;
    private static int validCountry = 0;
    private static int validBankName = 0;
    private static int validIban = 0;

    @Override
    protected void calculateSystemMetrics(String diagramPath, String instancePath) {
        metrics.append("\n## Bank-semantics\n");
        metrics.append("| Metric | Value |\n");
        metrics.append("| --- | --- |\n");
        metrics.append("| Number of accounts | TODO |\n");
    }

    @Override
    protected void saveSystemMetrics(String diagramPath, String instancePath) {
        metrics.append("| Total balance | TODO |\n");
        Utils.saveFile(metrics.toString(), instancePath, "metrics.md");
    }

}
