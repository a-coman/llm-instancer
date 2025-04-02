package es.uma.Metrics.Specific;

import java.util.ArrayList;

import es.uma.Utils;
import es.uma.Metrics.IMetrics;
import es.uma.Metrics.Utilities;

public class Restaurant implements IMetrics {

    private int validHomePhones, validDates;
    private ArrayList<String> invalidHomePhones, invalidDates;
    private int totalHomePhones, totalDates;

    public Restaurant() {
        validHomePhones = 0;
        validDates = 0;
        totalHomePhones = 0;
        totalDates = 0;
        invalidHomePhones = new ArrayList<>();
        invalidDates = new ArrayList<>();
    }

    @Override
    public void calculate(String diagramPath, String instancePath) {
        String instance = Utils.readFile(instancePath);
        ArrayList<String> homePhones = Utils.match(instance, "!\\s*\\w+\\s*\\.\\s*phoneNumber\\s*:=\\s*'\\s*([^']+)\\s*'");
        ArrayList<String> dates = Utils.match(instance, "!\\s*\\w+\\s*\\.\\s*(?:dateOfBirth|date)\\s*:=\\s*Date\\s*\\(\\s*'([^']+)'");

        System.out.println("Home Phones: " + homePhones);
        System.out.println("Dates: " + dates);

        dates.forEach(dateStr -> {
            totalDates++;
            
            if (Utilities.parseDate(dateStr) != null) {
                validDates++;
            } else {
                invalidDates.add(dateStr);
            }
        });

        homePhones.forEach((homePhone) -> {
            totalHomePhones++;
            if (Utilities.isValidPhone(homePhone)) {
                validHomePhones++;
            } else {
                invalidHomePhones.add(homePhone);
            }
        });


    }

    @Override
    public void aggregate(IMetrics otherMetrics) {
        if (!(otherMetrics instanceof Restaurant)) {
            return;
        }
        
        Restaurant other = (Restaurant) otherMetrics;
        this.validDates += other.validDates;
        this.validHomePhones += other.validHomePhones;

        this.totalDates += other.totalDates;
        this.totalHomePhones += other.totalHomePhones;
        
        this.invalidDates.addAll(other.invalidDates);
        this.invalidHomePhones.addAll(other.invalidHomePhones);

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Restaurant | Invalid | Total | Failure (%) | \n");
        sb.append("|---|---|---|---| \n");
        sb.append(Utilities.formatMetricRow("Dates", totalDates - validDates, totalDates))
          .append(Utilities.formatMetricRow("Phones", totalHomePhones - validHomePhones, totalHomePhones));
        
        sb.append(Utilities.getStringList("Failed Dates", invalidDates));
        sb.append(Utilities.getStringList("Failed Phones", invalidHomePhones));
        
        return sb.toString();
    }
    
    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant();
        restaurant.calculate("diagramPath", "src/main/resources/instances/Simple/restaurant/GPT_4O/02-04-2025--16-10-47/gen1/output.soil");
    }
}
