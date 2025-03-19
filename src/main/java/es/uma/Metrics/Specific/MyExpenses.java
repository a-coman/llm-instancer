package es.uma.Metrics.Specific;

import java.util.Map;

import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class MyExpenses implements IMetrics {

    private int validDates;
    private int totalDates;

    public MyExpenses() {
        validDates = 0;
        totalDates = 0;
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        String instance = Utils.readFile(instancePath);
        String datePattern = "!\\s*(\\w+)\\s*\\.\\s*(startDate|endDate)\\s*:=\\s*(.+)";
        Map<String, Map<String, String>> dates = Utilities.getMap(instance, datePattern);

        System.out.println(dates);

        dates.forEach((entity, attributes) -> {
            String startDate = attributes.get("startDate");
            String endDate = attributes.get("endDate");

            if (startDate != null && endDate != null) {
                totalDates++;
                if (endDate.compareTo(startDate) > 0) {
                    validDates++;
                }
            }
        });
    }

    @Override
    public void calculateInvalid(String diagramPath, String instancePath) {
        calculate(diagramPath, instancePath);        
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof MyExpenses)) {
            return;
        }
        
        MyExpenses other = (MyExpenses) otherMetrics;
        this.validDates += other.validDates;
        this.totalDates += other.totalDates;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| MyExpenses | Valid | Total | Success (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Dates", validDates, totalDates)); 
        return sb.toString();
    }

}