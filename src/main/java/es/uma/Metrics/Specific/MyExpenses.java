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

            LocalDate endDate = Utilities.parseDate(endDateStr);
            LocalDate startDate = Utilities.parseDate(startDateStr);

            if (startDate == null || endDate == null) {
                return;
            }

            totalDates++;
            if (endDate.compareTo(startDate) >= 0) {
                validDates++;
            } else {
                invalidDates.add("End date: " + endDate + " is before start date: " + startDate);
            }
        });
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
        sb.append("| MyExpenses | Invalid | Total | Failure (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Dates", totalDates - validDates, totalDates));
        sb.append(Utilities.getStringList("Invalid dates", invalidDates));
        return sb.toString();
    }

}