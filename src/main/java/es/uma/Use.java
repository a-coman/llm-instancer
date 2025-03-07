package es.uma;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Use {
    private Process process;

    public Use() {
        // Instantiate an use shell
        try {
            ProcessBuilder pb;
            // Check system type (bash/cmd)
            if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                pb = new ProcessBuilder("cmd.exe", "/c", "java -jar use-gui.jar -nogui");
            } else {
                pb = new ProcessBuilder("bash", "-c", "java -jar use-gui.jar -nogui");
            }

            pb.directory(new File("./src/main/resources/use-7.1.1/lib/"));
            // Check and add JAVA to path
            String javaPath = System.getenv("JAVA_HOME");
            if (javaPath == null) {
                throw new RuntimeException("Java path not found");
            }
            pb.environment().put("PATH", javaPath + "/bin");
            pb.redirectErrorStream(true);
            process = pb.start();
        } catch (Exception e) {
            System.err.println("Error starting use shell");
            throw new RuntimeException(e);
        }

    }

    public void close() {
        // Close the USE shell
        try {
            runCommand("exit");
            process.waitFor();
        } catch (InterruptedException e) {
            System.err.println("Error waiting for use shell to close");
            throw new RuntimeException(e);
        } finally {
            process.destroy();
        }
    }

    private void runCommand(String command) {
        // Run use commands
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            writer.write(command + "\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error running command: " + command);
            throw new RuntimeException(e);
        }
    }

    private String readOutput(String marker) {
        // Read the standard output until marker
        StringBuilder output = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null && !line.contains(marker)) {
                output.append(line + "\n");
            }
            return output.toString();
        } catch (IOException e) {
            System.err.println("Error reading output");
            throw new RuntimeException(e);
        }
    }

    private void open(String diagramPath, String instancePath) {
        // Open diagram and instance files     
        File diagramFile = new File(diagramPath).getAbsoluteFile();
        File instanceFile = new File(instancePath).getAbsoluteFile();

        runCommand("reset");
        runCommand("open " + diagramFile.getPath());
        runCommand("open " + instanceFile.getPath());
        runCommand("Open finalized"); // Marker
    }

    public String checkSyntax(String diagramPath, String instancePath) {
        Metrics.initializeSyntax(); // Mark the use of syntax checking for the agent
        open(diagramPath, instancePath);
        String output = readOutput("Open finalized");
        // Check for syntax errors
        StringBuffer errors = new StringBuffer();
        String[] searchStrings = {"<input>", "Error:", "Warning:"};
        for (String search : searchStrings) {
            int index = output.indexOf(search);
            while (index >= 0) {
                if (output.contains("natGNUReadline in java.library.path")) {  // Provisional, skip first console log error
                    index = output.indexOf(search, index + 1);
                    continue;
                }
                String error = output.substring(index+7, output.indexOf("\n", index)); // +7 to avoid input tag"
                Metrics.incrementSyntaxErrors();
                Metrics.addSyntaxError(error);
                errors.append(error + "\n");
                index = output.indexOf(search, index + 1);
            }
        }

        return errors.toString().isEmpty() ? "OK" : errors.toString();

    }

    // Count number of multiplicities failures
    private static int countOccurrences(String text, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        int count = 0;
        while (m.find()) {
            count++;
        }
        return count;
    }

    // Extract number of constraints/invariants failures
    private static int extractInvariantFailures(String text) {
        Pattern pattern = Pattern.compile("checked (\\d+) invariants? in ([\\d.]+s), (\\d+) failures?");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(3));
        }
        return 0;
    }

    public String checkRestrictions(String diagramPath, String instancePath, String invariants) {
        Metrics.initializeCheck(); // Mark the use of check restrictions for the agent
        open(diagramPath, instancePath);
        runCommand("check");
        runCommand("Check finalized"); // Marker
        
        String output = readOutput("Check finalized");
        
        // Trim result and return errors
        int start = output.indexOf("checking structure");
        output = output.substring(start);

        int multiplicitiesErrors = countOccurrences(output, "Multiplicity constraint violation");
        int invariantErrors = extractInvariantFailures(output);

        Metrics.incrementMultiplicitiesErrors(multiplicitiesErrors);
        Metrics.incrementInvariantErrors(invariantErrors);

        String result = "";
        if (output.contains("violation")) { // Multiplicities failed
            result = output;
        }
        if (output.contains("FAILED") || output.contains("N/A")) { // Constraints/invariants failed
            result = output + "\n" + invariants;
        }
        
        System.out.println(result);

        if (!result.isEmpty()) {
            Metrics.addCheckError(output);
            Metrics.incrementCheckErrors();
        }

        return result.isEmpty() ? "OK" : result;

    }

    // Main for testing purposes
    public static void main(String[] args) {
        Use use = new Use();

        System.out.println(use.checkRestrictions("./src/main/resources/prompts/bank/diagram.use", "./src/main/resources/instances/previous/bank/gemini_2.soil", "invariants_Placeholder"));

        use.close();
    }
}
