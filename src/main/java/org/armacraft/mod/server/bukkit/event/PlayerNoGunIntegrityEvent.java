package org.armacraft.mod.server.bukkit.event;

import org.armacraft.mod.wrapper.GunInfoWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerNoGunIntegrityEvent extends PlayerEvent {
    private GunInfoWrapper gunInfo;
    private static final HandlerList handlers = new HandlerList();

    public PlayerNoGunIntegrityEvent(Player who, GunInfoWrapper wrapper) {
        super(who);
        this.gunInfo = wrapper;
    }

    public GunInfoWrapper getGunInfo() {
        return gunInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
