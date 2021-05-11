package org.armacraft.mod.server.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

public class PlayerMissingFilesEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private final List<String> missingHashes;
	
	public PlayerMissingFilesEvent(Player who, List<String> missingHashes) {
		super(who);
		this.missingHashes = missingHashes;
	}
	
	public List<String> getMissingHashes() {
		return this.missingHashes;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
