package es.uma.Metrics.JudgeAnalysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import es.uma.Metrics.General;
import es.uma.Utils;

public class GeneralBatchRunner {

    private static final String DATASET_ROOT = "../llm-viewer/public/data/dataset/GPT_5_2";
    private static final String DIAGRAMS_ROOT = "./src/main/resources/prompts";
    private static final String INSTANCES_FILE = "./src/main/java/es/uma/Metrics/JudgeAnalysis/GeneralBatchRunnerInstances.txt";
    private static final String OUTPUT_FILE_DIR = "./src/main/java/es/uma/Metrics/JudgeAnalysis/";
    private static final String OUTPUT_FILE_NAME = "general-metrics-batch.md";

    static record InstanceRef(String label, String type, String system, String gen, String category) {
        String diagramPath() {
            return DIAGRAMS_ROOT + "/" + system + "/diagram.use";
        }

        String instancePath(String experimentDir) {
            if (type.equals("CoT")) {
                return experimentDir + "/" + gen + "/" + category + ".soil";
            }
            return experimentDir + "/" + gen + "/output.soil";
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US); // dots as decimal separator in reports
        List<InstanceRef> instances = loadInstances(Paths.get(INSTANCES_FILE));
        System.out.println("Loaded " + instances.size() + " instances");

        validateAll(instances);

        StringBuilder sb = new StringBuilder();
        General totals = new General();

        for (InstanceRef ref : instances) {
            String experimentDir = resolveExperimentDir(ref.type(), ref.system());
            String diagramPath = ref.diagramPath();
            String instancePath = ref.instancePath(experimentDir);

            System.out.println("Processing: " + ref.label());
            General g = new General();
            g.calculate(diagramPath, instancePath);

            sb.append("# ").append(ref.label()).append("\n\n");
            sb.append(g.toString()).append("\n\n");
            totals.aggregate(g);
        }

        sb.append("# Totals\n\n");
        sb.append(totals.toString());

        Utils.saveFile(sb.toString(), OUTPUT_FILE_DIR, OUTPUT_FILE_NAME, false);
        System.out.println("Done. Wrote " + OUTPUT_FILE_DIR + OUTPUT_FILE_NAME);
    }

    static List<InstanceRef> loadInstances(Path file) {
        if (!Files.isRegularFile(file)) {
            throw new RuntimeException("Instances file not found: " + file.toAbsolutePath());
        }

        Set<String> seen = new LinkedHashSet<>();
        List<InstanceRef> instances = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(file)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (!seen.add(line)) {
                    System.err.println("Duplicate line skipped: " + line);
                    continue;
                }
                instances.add(parseLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read instances file: " + file, e);
        }

        return instances;
    }

    static InstanceRef parseLine(String line) {
        String[] parts = line.split("\\s*/\\s*");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid instance line (need at least 4 segments): " + line);
        }

        String model = parts[0].trim();
        if (!model.equalsIgnoreCase("gpt_5_2")) {
            throw new IllegalArgumentException("Unsupported model (expected gpt_5_2): " + line);
        }

        String typeRaw = parts[1].trim().toLowerCase();
        String type = switch (typeRaw) {
            case "cot" -> "CoT";
            case "simple" -> "Simple";
            default -> throw new IllegalArgumentException("Invalid type (expected cot or simple): " + line);
        };

        String system = parts[2].trim();
        String gen = parts[3].trim();
        if (!gen.startsWith("gen")) {
            gen = "gen" + gen;
        }

        String category = null;
        if (type.equals("CoT")) {
            if (parts.length < 5) {
                throw new IllegalArgumentException("CoT instance missing category: " + line);
            }
            category = parts[4].trim();
        }

        return new InstanceRef(line, type, system, gen, category);
    }

    static String resolveExperimentDir(String type, String system) {
        Path systemDir = Paths.get(DATASET_ROOT, type, system);
        if (!Files.isDirectory(systemDir)) {
            throw new RuntimeException("System directory not found: " + systemDir);
        }

        List<Path> timestamps;
        try (Stream<Path> stream = Files.list(systemDir)) {
            timestamps = stream
                    .filter(Files::isDirectory)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to list experiment dirs: " + systemDir, e);
        }

        if (timestamps.isEmpty()) {
            throw new RuntimeException("No experiment timestamp folder under: " + systemDir);
        }
        if (timestamps.size() > 1) {
            throw new RuntimeException(
                    "Multiple experiment folders under " + systemDir + ": " + timestamps);
        }

        return timestamps.get(0).toString();
    }

    static void validateAll(List<InstanceRef> instances) {
        List<String> missing = new ArrayList<>();

        for (InstanceRef ref : instances) {
            Path diagram = Paths.get(ref.diagramPath());
            if (!Files.isRegularFile(diagram)) {
                missing.add("diagram: " + diagram);
            }

            String experimentDir;
            try {
                experimentDir = resolveExperimentDir(ref.type(), ref.system());
            } catch (RuntimeException e) {
                missing.add(ref.label() + " -> " + e.getMessage());
                continue;
            }

            Path instance = Paths.get(ref.instancePath(experimentDir));
            if (!Files.isRegularFile(instance)) {
                missing.add("instance: " + instance);
            }
        }

        if (!missing.isEmpty()) {
            System.err.println("Pre-flight validation failed:");
            missing.forEach(p -> System.err.println("  " + p));
            throw new RuntimeException("Missing " + missing.size() + " file(s). Aborting.");
        }

        System.out.println("Pre-flight validation passed for " + instances.size() + " instances");
    }
}
