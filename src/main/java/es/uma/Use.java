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
        StringBuffer errors = new StringBuffer();
        open(diagramPath, instancePath);
        String output = readOutput("Open finalized");
    
        String pattern = "(.*?\\n)?(.*?\\n)?(<input>:.*?\\n|Error:.*?\\n|Warning:.*?\\n|INTERNAL ERROR:.*?\\n)";
    
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(output);
    
        while (matcher.find()) {
            String previousLine1 = matcher.group(1);  // First previous line (optional)
            String previousLine2 = matcher.group(2);  // Second previous line (optional)
            String errorLine = matcher.group(3);      // Error line
    
            if (errorLine != null && !errorLine.contains("GNUReadline in java.library.path")) {
                if (previousLine1 != null && !previousLine1.equals("\n")) {
                    errors.append(previousLine1);
                }
                if (previousLine2 != null && !previousLine1.equals("\n")) {
                    errors.append(previousLine2);
                }
                errors.append(errorLine).append("\n");
            }
        }
    
        String syntaxErrors = errors.toString().trim().isEmpty() ? "OK" : errors.toString().trim();
        System.out.println(syntaxErrors);
        return syntaxErrors;
    }

    public String checkMultiplicities(String diagramPath, String instancePath) {
        open(diagramPath, instancePath);
        runCommand("check");
        runCommand("Check finalized"); // Marker
        
        String output = readOutput("Check finalized");
        
        // Trim result and return errors
        int start = output.indexOf("checking structure...");
        int end = output.indexOf("checked structure");
        output = output.substring(start, end);

        String result = "";
        if (output.contains("violation")) { // Multiplicities failed
            result = output;
        }

        String multiplicitiesErrors = result.trim().isEmpty() ? "OK" : result.trim();
        System.out.println(multiplicitiesErrors);
        return multiplicitiesErrors;
    }

    public String checkInvariants(String diagramPath, String instancePath, String invariants) {
        open(diagramPath, instancePath);
        runCommand("check");
        runCommand("Check finalized"); // Marker
        
        String output = readOutput("Check finalized");
        
        // Trim result and return errors
        int start = output.indexOf("checking invariants...");
        output = output.substring(start);

        String result = "";

        // To match also N/A -> || output.contains("N/A")
        if (output.contains("FAILED")) { // Constraints/invariants failed
            result = invariants.isEmpty() ? output : output + "\n" + invariants;
        }

        String invariantsErrors = result.trim().isEmpty() ? "OK" : result.trim();
        System.out.println(invariantsErrors);
        return invariantsErrors;
    }

    // Main for testing purposes
    public static void main(String[] args) {
        String testDiagram = "./src/main/resources/prompts/bank/diagram.use";
        String testInstance = "./src/main/resources/instances/CoT/bank/GPT_4O/11-03-2025--18-41-54/gen2/outputValid.soil";

        Use use = new Use();
        use.checkSyntax(testDiagram, testInstance);
        System.out.println("\n\n");

        use.checkMultiplicities(testDiagram, testInstance);
        System.out.println("\n\n");

        use.checkInvariants(testDiagram, testInstance, "");
        System.out.println("\n\n");
        use.close();
    }
}
