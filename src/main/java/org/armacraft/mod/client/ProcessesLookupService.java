package org.armacraft.mod.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.armacraft.mod.client.processes.ProcessGrabber;
import org.armacraft.mod.wrapper.ProcessWrapper;
import org.armacraft.mod.util.MiscUtil;

import com.google.common.collect.ImmutableSet;

enum ProcessesLookupService {

	INSTANCE;
	
	private Thread thread;
	private Set<ProcessWrapper> processes = Collections.synchronizedSet(new HashSet<>());
	
	private synchronized boolean startIfNotAlready() {
		if (this.thread != null && this.thread.isAlive()) {
			return false;
		}
		this.thread = new Thread(() -> {
			final String osName = System.getProperty("os.name");
			while(true) {
				ProcessGrabber grabber = ProcessGrabber.getDefaultImplementationFor(osName);
				try {
					Set<ProcessWrapper> processesFound = grabber.getCurrentProcesses();
					this.processes.clear();
					this.processes.addAll(processesFound);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				MiscUtil.silentlySleep(1000L);
			}
		});
		this.thread.start();
		return true;
	}
	
	public synchronized Set<ProcessWrapper> getCurrentProcesses() {
		this.startIfNotAlready();
		// Cópia imutável, de preferência
		return ImmutableSet.copyOf(this.processes);
	}
}
