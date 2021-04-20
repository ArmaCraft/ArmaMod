package org.armacraft.mod.environment;

public class ProcessWrapper {
    private String name;
    private long pid;
    private int ramUsage;

    public ProcessWrapper(String name, long pid, int ramUsage) {
        this.name = name;
        this.pid = pid;
        this.ramUsage = ramUsage;
    }

    public static ProcessWrapper ofRawLine(String line) {
        line = line.trim().replaceAll("\\s{2,}", " ");
        String[] specs = line.split(" ");
        return new ProcessWrapper(specs[0], Long.parseLong(specs[1]), Integer.parseInt(specs[4].replace(".", "")));
    }

    public static ProcessWrapper fromSimpleString(String line) {
        String[] specs = line.split("%");
        return new ProcessWrapper(specs[0], Long.parseLong(specs[1]), Integer.parseInt(specs[4]));
    }

    public String getName() {
        return name;
    }

    public long getPid() {
        return pid;
    }

    public int getRamUsageInKBytes() {
        return ramUsage;
    }

    @Override
    public String toString() {
        return name + "%" + pid + "%" + ramUsage;
    }
}
