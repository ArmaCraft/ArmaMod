package org.armacraft.mod;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent.ClientTickEvent;

public enum GameIntegrity {
	INSTANCE;
	
	private int tickCount = 0;
	
	// Fazer com base em 30 segundos, se 0.1 superior a 20 é kick
	private int averageClientTPS = 0;
	
	private boolean hasAdditionalResourcepack;
	
	private boolean modsOrBinGotChangedWhileRunning;
	
	private Map<String, String> foldersHashes;
	
	public void sendToServer() {
		
	}
	
	public void onTick(ClientTickEvent event) {
		if (++tickCount % 20 == 0) {
			this.sendToServer();
		}
		
		Minecraft.getInstance().getResourcePackRepository().getAvailablePacks().forEach(pack -> {
			System.out.println(pack.getId());
		});
	}
}
