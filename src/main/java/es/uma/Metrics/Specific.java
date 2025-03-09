package es.uma.Metrics;

public abstract class Specific {
    
    private static Specific getSpecific(String system) {
        switch (system.toLowerCase()) {
            case "bank":
                return new Bank();
            // Add more cases here for other systems
            default:
                throw new IllegalArgumentException("Unknown system: " + system);
        }
    }
    
    public static void calculateMetrics(String diagramPath, String instancePath, String system) {
        Specific specificSystem = getSpecific(system);
        specificSystem.calculateSystemMetrics(diagramPath, instancePath);
    }

    public static void saveMetrics(String diagramPath, String instancePath, String system) {
        Specific specificSystem = getSpecific(system);
        specificSystem.saveSystemMetrics(diagramPath, instancePath);
    }
    
    // Abstract method that each specific system must implement
    protected abstract void calculateSystemMetrics(String diagramPath, String instancePath);
    protected abstract void saveSystemMetrics(String diagramPath, String instancePath);
}
