package es.uma;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String readFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            System.err.println("Error reading file: " + path + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void saveFile(String file, String path, String filename, boolean append) {
        try {
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            FileWriter writer = new FileWriter(path + filename, append);
            writer.write(file);
            writer.flush();
            writer.close();
            System.out.println("Saved at " + path + filename);
        } catch (Exception e) {
            System.err.println("Error writing to file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Append true by default
    public static void saveFile(String file, String path, String filename) {
        saveFile(file, path, filename, true);
    }
    

    public static String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy--HH-mm-ss")); 
    }

    public static String removeBackticks(String text) {
        String regex = "```+[^\\n]*"; // Matches codeblocks, i.e. ``` or more followed by any character except a newline
        return text.replaceAll(regex, "").trim();
    }

    public static String removeComments(String text) {
        String regex = "(?m)^(?!!|--).*$"; // Matches any line that doesn't start with ! or --
        return text.replaceAll(regex, "").trim();
    }

    public static void waitFor(int minutes) {        
        // Wait for minutes
        try {
            System.out.println("\n\nWaiting for " + minutes + " before generating the next instance...\n");
            TimeUnit.MINUTES.sleep(minutes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Match by pattern (Returns the first captured group)
    public static ArrayList<String> match(String text, String pattern) {
        ArrayList<String> list = new ArrayList<>();
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        while (m.find()) {
            list.add(m.group(1).trim());
        }
        return list;
    }

    // Split by pattern (Returns entire line matched)
    public static ArrayList<String> split(String text, String pattern) {
        ArrayList<String> list = new ArrayList<>();
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        while (m.find()) {
            list.add(m.group().trim());
        }
        return list;
    }


    public static boolean validMatch(String text, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        return m.matches();
    }


    public static void main(String[] args) {
        String model = "test/";
        Utils.saveFile("Hello World!", "./src/main/resources/instances/" + model + Utils.getTime(), "/output.md");
    }
}
