package org.armacraft.mod.server.bukkit.event;

import org.armacraft.mod.environment.EnvironmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerSentEnvironmentEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private EnvironmentWrapper environment;

    public PlayerSentEnvironmentEvent(Player who, EnvironmentWrapper environment) {
        super(who);
        this.environment = environment;
    }

    public EnvironmentWrapper getEnvironment() {
        return this.environment;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

