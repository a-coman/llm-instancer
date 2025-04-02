package es.uma.Metrics;

import es.uma.Utils;

public class Extract {
    public static void main(String[] args) {
        String pathFoot = "src/main/resources/instances/CoT/football/GPT_4O/02-04-2025--19-03-28/";
        String file = Utils.readFile(pathFoot + "metrics.md");
        
        StringBuilder sb = new StringBuilder();

        for (String line : file.split("\n")) {
            if (line.contains(".name")) {
                sb.append(line).append("\n");
            }
        }

        Utils.saveFile(sb.toString(), pathFoot, "names.md", false);


        // Restaurant
        String pathRest = "src/main/resources/instances/Simple/restaurant/GPT_4O/02-04-2025--16-10-47/";
        file = Utils.readFile(pathRest + "metrics.md");
        
        StringBuilder sbNames = new StringBuilder();
        StringBuilder sbLicenses = new StringBuilder();
        StringBuilder sbFood = new StringBuilder();

        for (String line : file.split("\n")) {
            if (line.contains(".name") && !line.contains("reservation") && !line.contains("banquet") && !line.contains("individual")) {
                sbNames.append(line).append("\n");
            }
            if (line.contains(".driverLicenseNr")) {
                sbLicenses.append(line).append("\n");
            }
            if (line.contains(".description") && !line.contains("table")) {
                sbFood.append(line).append("\n");
            }
        }

        Utils.saveFile(sbNames.toString(), pathRest, "names.md", false);
        Utils.saveFile(sbLicenses.toString(), pathRest, "licenses.md", false);
        Utils.saveFile(sbFood.toString(), pathRest, "food.md", false);
    }
}
