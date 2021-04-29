package org.armacraft.mod.client.processes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.armacraft.mod.wrapper.ProcessWrapper;

public enum LinuxProcessGrabber implements ProcessGrabber {
	INSTANCE;

	@Override
	public Set<ProcessWrapper> getCurrentProcesses() throws Exception {
		Process process = Runtime.getRuntime().exec("ps -e");
		
		Set<ProcessWrapper> processes = new HashSet<>();
		
		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		while ((line = input.readLine()) != null) {
			processes.clear();
			processes.add(ProcessWrapper.ofLinux(line));
		}
		
		return processes;
	}
}
