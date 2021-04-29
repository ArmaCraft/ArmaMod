package org.armacraft.mod.wrapper;

import java.util.Set;

public class EnvironmentWrapper {
    private String operationalSystem;
    private String javaVersion;
    private Set<ProcessWrapper> runningProcesses;

    public EnvironmentWrapper(String operationalSystem, String javaVersion, Set<ProcessWrapper> runningProcesses) {
        this.operationalSystem = operationalSystem;
        this.javaVersion = javaVersion;
        this.runningProcesses = runningProcesses;
    }

    public String getOperationalSystem() {
        return operationalSystem;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public Set<ProcessWrapper> getRunningProcesses() {
        return runningProcesses;
    }
}
