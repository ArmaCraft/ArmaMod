package org.armacraft.mod.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerDashEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();
	
	public PlayerDashEvent(Player who) {
		super(who);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
