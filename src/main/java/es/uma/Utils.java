package es.uma;

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

    public static void saveFile(String file, String path, String filename) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(file);
            System.out.println("Response saved at " + path);
        } catch (Exception e) {
            System.err.println("Error writing to file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy -- HH-mm-ss")); 
    }

}
