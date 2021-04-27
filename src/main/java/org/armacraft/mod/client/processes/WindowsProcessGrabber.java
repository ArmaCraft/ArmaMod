package org.armacraft.mod.client.processes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.armacraft.mod.environment.ProcessWrapper;

public enum WindowsProcessGrabber implements ProcessGrabber {
	INSTANCE;

	@Override
	public Set<ProcessWrapper> getCurrentProcesses() throws Exception {
		Process process = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe /v /fo csv");
		
		Set<ProcessWrapper> processes = new HashSet<>();
		
		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		boolean isFirstLine = true;
		while ((line = input.readLine()) != null) {
			if (isFirstLine) {
				isFirstLine = false;
				continue; // Pula
			}
			processes.add(ProcessWrapper.ofWindowsCSV(line));
		}
		
		return processes;
	}

}
