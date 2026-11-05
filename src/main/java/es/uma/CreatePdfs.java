package es.uma;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CreatePdfs {
    public static void main(String[] args) {
        // Paths
        String instancesRootPath = "/home/aac/Repos/llm-viewer/public/data/dataset/GPT_5_2";
        String diagramsRootPath = "/home/aac/Repos/llm-viewer/public/data/prompts";

        File rootDir = new File(instancesRootPath);
        if (!rootDir.exists()) {
            System.err.println("Root directory does not exist: " + instancesRootPath);
            return;
        }

        System.out.println("Starting PDF creation...");
        System.out.println("Instances Root: " + instancesRootPath);
        System.out.println("Diagrams Root: " + diagramsRootPath);

        Use use = new Use();
        try (Stream<Path> paths = Files.walk(Paths.get(instancesRootPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".soil"))
                    .forEach(path -> {
                        try {
                            createPdf(use, path, Paths.get(instancesRootPath), Paths.get(diagramsRootPath));
                        } catch (Exception e) {
                            System.err.println("Error processing file: " + path);
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished.");
    }

    private static void createPdf(Use use, Path instancePath, Path instancesRoot, Path diagramsRoot) {
        // Extract relative path to determine model name
        // Structure: /mnt/c/.../GPT_4O_exp1/{Simple|CoT}/{ModelName}/...
        Path relativePath = instancesRoot.relativize(instancePath);

        // We expect at least {Type}/{ModelName}/chunk...
        if (relativePath.getNameCount() < 2) {
            System.err.println("Skipping path with unexpected structure: " + relativePath);
            return;
        }

        // getName(0) is Type (Simple/CoT), getName(1) is ModelName
        String modelName = relativePath.getName(1).toString();

        // Diagram directory names are typically lowercase in prompts
        String diagramDirName = modelName.toLowerCase();

        Path diagramPath = diagramsRoot.resolve(diagramDirName).resolve("diagram.use");

        if (!Files.exists(diagramPath)) {
            System.err.println("Diagram not found for model '" + modelName + "' at: " + diagramPath);
            return;
        }

        System.out.println("Generating PDF for: " + instancePath);
        // System.out.println("Using diagram: " + diagramPath);

        use.instanceToPdf(diagramPath.toString(), instancePath.toString());
    }
}
