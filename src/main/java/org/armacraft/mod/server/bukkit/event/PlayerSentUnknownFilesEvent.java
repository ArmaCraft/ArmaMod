package org.armacraft.mod.server.bukkit.event;

import java.util.List;

import org.armacraft.mod.network.dto.FileInfoDTO;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerSentUnknownFilesEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private final List<FileInfoDTO> unknownFiles;
	
	public PlayerSentUnknownFilesEvent(Player who, List<FileInfoDTO> unknownFiles) {
		super(who);
		this.unknownFiles = unknownFiles;
	}
	
	public List<FileInfoDTO> getUnknownFiles() {
		return this.unknownFiles;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
