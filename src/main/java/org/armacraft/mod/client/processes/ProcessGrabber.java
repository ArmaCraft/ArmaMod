package org.armacraft.mod.client.processes;

import org.armacraft.mod.wrapper.ProcessWrapper;

import java.util.Set;

public interface ProcessGrabber {

	Set<ProcessWrapper> getCurrentProcesses() throws Exception;
	
	public static ProcessGrabber getDefaultImplementationFor(String osName) {
		if (osName.toLowerCase().contains("win")) {
			return WindowsProcessGrabber.INSTANCE;
		}
		
		// Existe MacOS tbm, mas fodase ngm usa
		return LinuxProcessGrabber.INSTANCE;
	}
}
