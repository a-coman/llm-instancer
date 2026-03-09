package es.uma;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class RawConsoleLogs {
    private static final String DEFAULT_OUTPUT_PATH = "./target/";

    private RawConsoleLogs() {
    }

    public static void run(ThrowingConsumer contextConsumer) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream rawConsoleBuffer = new ByteArrayOutputStream();
        Context context = new Context();
        Throwable failure = null;

        redirectConsole(originalOut, originalErr, rawConsoleBuffer);

        try {
            contextConsumer.accept(context);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            failure = throwable;
        } finally {
            saveRawLogs(rawConsoleBuffer, context.getOutputPath(), originalErr);
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        if (failure != null) {
            rethrow(failure);
        }
    }

    private static void redirectConsole(PrintStream originalOut, PrintStream originalErr,
            ByteArrayOutputStream rawConsoleBuffer) {
        System.setOut(
                new PrintStream(new TeeOutputStream(originalOut, rawConsoleBuffer), true, StandardCharsets.UTF_8));
        System.setErr(
                new PrintStream(new TeeOutputStream(originalErr, rawConsoleBuffer), true, StandardCharsets.UTF_8));
    }

    private static void saveRawLogs(ByteArrayOutputStream rawConsoleBuffer, String outputPath,
            PrintStream originalErr) {
        String rawLogs = "# Raw console output\n\n```text\n"
                + sanitize(rawConsoleBuffer.toString(StandardCharsets.UTF_8))
                + "\n```\n";

        try {
            Utils.saveFile(rawLogs, outputPath, "raw_logs.md", false);
        } catch (RuntimeException saveException) {
            originalErr.println("Failed to save raw_logs.md: " + saveException.getMessage());
        }
    }

    private static String sanitize(String rawConsole) {
        String normalized = rawConsole.replace("\r\n", "\n").replace('\r', '\n');

        String trimmedLines = Arrays.stream(normalized.split("\n", -1))
                .map(String::strip)
                .collect(Collectors.joining("\n"));

        String collapsedBodyGaps = trimmedLines.replaceAll("(?m)(- body:\\n)(?:\\n){2,}", "$1\\n");

        return collapsedBodyGaps.strip();
    }

    private static void rethrow(Throwable throwable) {
        if (throwable instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }

        if (throwable instanceof Error error) {
            throw error;
        }

        throw new RuntimeException(throwable);
    }

    @FunctionalInterface
    public interface ThrowingConsumer {
        void accept(Context context) throws Exception;
    }

    public static final class Context {
        private String outputPath = DEFAULT_OUTPUT_PATH;

        public void setOutputPath(String outputPath) {
            if (outputPath == null || outputPath.isBlank()) {
                this.outputPath = DEFAULT_OUTPUT_PATH;
                return;
            }

            this.outputPath = outputPath;
        }

        private String getOutputPath() {
            return outputPath;
        }
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