package es.uma;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Finds the modified USE 7.5.0 build (headless PDF export) used by {@link Use}.
 *
 * Uses src/main/resources/use-7.5.0 if it is already there. Otherwise downloads the v7.5.0-export
 * release from github.com/a-coman/use and unzips it there.
 *
 * This file is kept identical in llm-instancer and llm-evaluator.
 */
public final class UseLocator {

    private static final String USE_DIR = "use-7.5.0";
    private static final String RELEASE_URL = "https://github.com/a-coman/use/releases/download/v7.5.0-export/use-7.5.0-export-bin.zip";

    private static final Path RESOURCES_DIR = Paths.get("src", "main", "resources");

    private static Path home;

    private UseLocator() {
    }

    /** Directory that contains use-gui.jar. */
    public static Path libDir() {
        return home().resolve("lib");
    }

    /** Root of the USE distribution (the folder holding bin/, lib/, ...). */
    public static synchronized Path home() {
        if (home == null) {
            home = locate().toAbsolutePath().normalize();
            System.out.println("Using USE from " + home);
        }
        return home;
    }

    private static Path locate() {
        try {
            Path resourcesUse = RESOURCES_DIR.resolve(USE_DIR);
            if (isUseHome(resourcesUse)) {
                return resourcesUse;
            }

            return download(resourcesUse);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Could not locate the USE 7.5.0 build", e);
        }
    }

    private static Path download(Path target) throws IOException, InterruptedException {
        System.out.println("USE not found. Downloading " + RELEASE_URL);
        Files.createDirectories(RESOURCES_DIR);
        Path zip = Files.createTempFile("use-7.5.0-export-bin", ".zip");
        try {
            HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
            HttpRequest request = HttpRequest.newBuilder(URI.create(RELEASE_URL)).GET().build();
            HttpResponse<Path> response = client.send(request,
                    HttpResponse.BodyHandlers.ofFile(zip));
            if (response.statusCode() != 200) {
                throw new IOException("Download failed with HTTP " + response.statusCode() + ": " + RELEASE_URL);
            }
            deleteRecursively(target);
            unzip(zip, RESOURCES_DIR);
        } finally {
            Files.deleteIfExists(zip);
        }
        if (!isUseHome(target)) {
            throw new IOException("Downloaded archive did not contain " + USE_DIR + "/lib/use-gui.jar");
        }
        return target;
    }

    private static boolean isUseHome(Path dir) {
        return Files.isRegularFile(dir.resolve("lib").resolve("use-gui.jar"));
    }

    private static void unzip(Path zip, Path destination) throws IOException {
        Path root = destination.toAbsolutePath().normalize();
        Files.createDirectories(root);
        try (InputStream in = Files.newInputStream(zip); ZipInputStream zin = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                Path out = root.resolve(entry.getName()).normalize();
                if (!out.startsWith(root)) {
                    throw new IOException("Zip entry outside target directory: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(out);
                } else {
                    Files.createDirectories(out.getParent());
                    Files.copy(zin, out, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void deleteRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (var paths = Files.walk(dir)) {
            for (Path p : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                Files.delete(p);
            }
        }
    }
}
