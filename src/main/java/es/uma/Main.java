package es.uma;

import es.uma.Metrics.MetricsRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream rawConsoleBuffer = new ByteArrayOutputStream();

        System.setOut(
                new PrintStream(new TeeOutputStream(originalOut, rawConsoleBuffer), true, StandardCharsets.UTF_8));
        System.setErr(
                new PrintStream(new TeeOutputStream(originalErr, rawConsoleBuffer), true, StandardCharsets.UTF_8));

        Experiment experiment = null;
        try {
            experiment = new Experiment(Model.GPT_5_2, "CoT", "addressbook", 30, Size.NONE);
            experiment.run();
            Logger.save(experiment.getInstancePath());
            MetricsRunner metricsRunner = new MetricsRunner();
            metricsRunner.run(experiment);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            String outputPath = "./target/";
            if (experiment != null) {
                outputPath = experiment.getInstancePath();
            }

            String sanitizedConsole = sanitizeRawConsole(rawConsoleBuffer.toString(StandardCharsets.UTF_8));

            String rawLogs = "# Raw console output\n\n```text\n"
                    + sanitizedConsole
                    + "\n```\n";

            try {
                Utils.saveFile(rawLogs, outputPath, "raw_logs.md", false);
            } catch (Exception saveException) {
                originalErr.println("Failed to save raw_logs.md: " + saveException.getMessage());
            }

            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    private static String sanitizeRawConsole(String rawConsole) {
        String normalized = rawConsole.replace("\r\n", "\n").replace('\r', '\n');

        String trimmedLines = Arrays.stream(normalized.split("\n", -1))
                .map(String::strip)
                .collect(Collectors.joining("\n"));

        String collapsedBodyGaps = trimmedLines.replaceAll("(?m)(- body:\\n)(?:\\n){2,}", "$1\\n");

        return collapsedBodyGaps.strip();
    }

    private static final class TeeOutputStream extends OutputStream {
        private final OutputStream first;
        private final OutputStream second;

        private TeeOutputStream(OutputStream first, OutputStream second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public void write(int b) throws IOException {
            first.write(b);
            second.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            first.write(b, off, len);
            second.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            first.flush();
            second.flush();
        }

        @Override
        public void close() throws IOException {
            flush();
        }
    }
}
