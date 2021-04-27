package org.armacraft.mod.server.bukkit.event;

import org.armacraft.mod.wrapper.GunInfoWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Optional;

public class PlayerNoGunIntegrityEvent extends PlayerEvent {
    private GunInfoWrapper clientInfos;
    private Optional<GunInfoWrapper> serverInfos;
    private static final HandlerList handlers = new HandlerList();

    public PlayerNoGunIntegrityEvent(Player who, GunInfoWrapper clientInfos, Optional<GunInfoWrapper> serverInfos) {
        super(who);
        this.clientInfos = clientInfos;
        this.serverInfos = serverInfos;
    }

    public GunInfoWrapper getClientGunInfos() {
        return clientInfos;
    }

    public Optional<GunInfoWrapper> getServerGunInfos() {
        return serverInfos;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
