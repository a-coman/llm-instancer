package es.uma.Metrics.Specific;

import java.util.ArrayList;

import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class Football implements IMetrics {

    private int validDates;
    private ArrayList<String> invalidDates;
    private int totalDates;

    public Football() {
        validDates = 0;
        totalDates = 0;
        invalidDates = new ArrayList<>();
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        String instance = Utils.readFile(instancePath);
        ArrayList<String> dates = Utils.match(instance, "!\\s*\\w+\\s*\\.\\s*(?:date|startDate|endDate)\\s*:=\\s*'([^']+)'");
        
        dates.forEach( dateStr -> {
            totalDates++;
            if(Utilities.parseDate(dateStr) != null) {
                validDates++;
            } else {
                invalidDates.add(dateStr);
            }
        });
    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof Football)) {
            return;
        }
        
        Football other = (Football) otherMetrics;
        this.validDates += other.validDates;
        this.totalDates += other.totalDates;
        this.invalidDates.addAll(other.invalidDates);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Football | Invalid | Total | Failure (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Dates", totalDates - validDates, totalDates));

        sb.append(Utilities.getStringList("Failed dates", invalidDates));
        
        return sb.toString();
    }
    
}
