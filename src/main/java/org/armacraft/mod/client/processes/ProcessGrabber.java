package org.armacraft.mod.client.processes;

import java.util.Set;

import org.armacraft.mod.wrapper.ProcessWrapper;

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
