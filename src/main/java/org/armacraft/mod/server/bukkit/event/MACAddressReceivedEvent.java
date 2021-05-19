package org.armacraft.mod.server.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class MACAddressReceivedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private byte[] address;

    public MACAddressReceivedEvent(Player who, byte[] address) {
        super(who);
        this.address = address;
    }

    public byte[] getAddress() {
        return address;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
