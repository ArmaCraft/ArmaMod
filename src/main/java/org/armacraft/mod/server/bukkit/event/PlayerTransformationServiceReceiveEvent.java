package org.armacraft.mod.server.bukkit.event;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTransformationServiceReceiveEvent extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	private final List<String> transformationServices;
	
	public PlayerTransformationServiceReceiveEvent(Player who, List<String> tranformationServices) {
		super(who);
		this.transformationServices = tranformationServices;
	}
	
	public List<String> getTransformationServices() {
		return this.transformationServices;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
