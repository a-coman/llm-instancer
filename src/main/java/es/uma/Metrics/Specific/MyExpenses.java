package es.uma.Metrics.Specific;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class MyExpenses implements IMetrics {

    private int validDates;
    private ArrayList<String> invalidDates;
    private int totalDates;

    public MyExpenses() {
        validDates = 0;
        totalDates = 0;
        invalidDates = new ArrayList<>();
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        String instance = Utils.readFile(instancePath);
        String datePattern = "!\\s*(\\w+)\\s*\\.\\s*(startDate|endDate)\\s*:=\\s*Date\\('([^']+)'\\)";
        Map<String, Map<String, String>> dates = Utilities.getMap(instance, datePattern);

        System.out.println(dates);

        dates.forEach((entity, attributes) -> {
            String startDateStr = attributes.get("startDate");
            String endDateStr = attributes.get("endDate");

            if (startDateStr == null || endDateStr == null) {
                return;
            }

            LocalDate endDate = LocalDate.parse(endDateStr);
            LocalDate startDate = LocalDate.parse(startDateStr);

            totalDates++;
            if (endDate.compareTo(startDate) >= 0) {
                validDates++;
            } else {
                invalidDates.add("End date: " + endDate + " is before start date: " + startDate);
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
        this.invalidDates.addAll(other.invalidDates);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| MyExpenses | Valid | Total | Success (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Dates", validDates, totalDates));
        sb.append(Utilities.getStringList("Invalid dates", invalidDates));
        return sb.toString();
    }

}