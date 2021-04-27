package org.armacraft.mod.wrapper;

public class ProcessWrapper {
    private String name;
    private long pid;
    private String windowTitle;

    public ProcessWrapper(String name, long pid, String windowTitle) {
        this.name = name;
        this.pid = pid;
        this.windowTitle = windowTitle;
    }
    
    public static ProcessWrapper ofWindowsCSV(String line) {
        line = line.trim(); 
        String[] specs = line.split(",");
        String processName = removeQuotes(specs[0]);
        long pid = Long.parseLong(removeQuotes(specs[1]));
        String windowTitle = removeQuotes(specs[8]);
        return new ProcessWrapper(processName, pid, windowTitle);
    }

    public static ProcessWrapper ofLinux(String line) {
        line = line.trim().replaceAll("\\s{2,}", " ");
        String[] specs = line.split(" ");
        return new ProcessWrapper(specs[0], Long.parseLong(specs[1]), "");
    }

    public static ProcessWrapper fromSimpleString(String line) {
        String[] specs = line.split("%");
        return new ProcessWrapper(specs[0], Long.parseLong(specs[1]), specs[2]);
    }

    public String getName() {
        return name;
    }
    
    public String getMainWindowTitle() {
    	return this.windowTitle;
    }

    public long getPid() {
        return pid;
    }

    @Override
    public String toString() {
        return name + "%" + pid + "%" + this.windowTitle;
    }
    
    private static String removeQuotes(String str) {
    	return str.substring(1, str.length() - 1);
    }
}
