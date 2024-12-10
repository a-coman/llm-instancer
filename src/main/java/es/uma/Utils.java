package es.uma;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            System.out.println("Response saved at " + path + filename);
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

    public static void main(String[] args) {
        String model = "test/";
        Utils.saveFile("Hello World!", "./src/main/resources/instances/" + model + Utils.getTime(), "/output.md");
    }
}
