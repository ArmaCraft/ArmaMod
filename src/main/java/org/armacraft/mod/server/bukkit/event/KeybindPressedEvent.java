package org.armacraft.mod.server.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class KeybindPressedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private Character key;

    public KeybindPressedEvent(Player who, Character key) {
        super(who);
        this.key = key;
    }

    public Character getKey() {
        return key;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
