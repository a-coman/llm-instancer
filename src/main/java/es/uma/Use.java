package es.uma;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Use {
    private Process process;

    public Use() {
        // Instantiate an use shell
        try {
            ProcessBuilder pb;
            if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                pb = new ProcessBuilder("cmd.exe", "/c", "java -jar use-gui.jar -nogui");
            } else {
                pb = new ProcessBuilder("bash", "-c", "java -jar use-gui.jar -nogui");
            }

            pb.directory(new File("./src/main/resources/use-7.1.1/lib/"));
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
        // Close the use shell
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

    public String readOutput(String marker) {
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

    public String check(String diagramPath, String instancePath) {
        // Open diagram and instance files
        runCommand("open " + diagramPath);
        runCommand("open " + instancePath);
        runCommand("check");
        runCommand("Check finalized");
        
        // Read output
        String output = readOutput("Check finalized");
        
        // Trim result and return errors
        int start = output.indexOf("checking structure");
        output = output.substring(start);
        if (output.contains("violation") || output.contains("FAILED") || output.contains("N/A"))
            return output;
        else
            return "OK";

    }

    // Main for testing purposes
    public static void main(String[] args) {
        Use use = new Use();
        
        System.out.println(use.check("/home/andrei/Repos/llm-instancer/src/main/resources/prompts/bank/diagram.use", "/home/andrei/Repos/llm-instancer/src/main/resources/instances/previous/bank/gemini_1.soil"));

        System.out.println(use.check("/home/andrei/Repos/llm-instancer/src/main/resources/prompts/bank/diagram.use", "/home/andrei/Repos/llm-instancer/src/main/resources/instances/previous/bank/gemini_2.soil"));

        use.close();
    }
}
