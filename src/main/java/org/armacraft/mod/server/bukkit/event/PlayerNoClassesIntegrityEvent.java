package org.armacraft.mod.server.bukkit.event;

import org.armacraft.mod.environment.EnvironmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

public class PlayerNoClassesIntegrityEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private List<String> expectedHashs;
    private String playerHash;

    public PlayerNoClassesIntegrityEvent(Player who, String playerHash, List<String> expectedHashs) {
        super(who);
        this.expectedHashs = expectedHashs;
        this.playerHash = playerHash;
    }

    public List<String> getExpectedHashs() {
        return expectedHashs;
    }

    public String getPlayerHash() {
        return playerHash;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}