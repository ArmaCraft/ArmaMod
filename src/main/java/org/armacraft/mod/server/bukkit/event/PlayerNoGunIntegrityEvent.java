package org.armacraft.mod.server.bukkit.event;

import org.armacraft.mod.wrapper.ClientGunInfoWrapper;
import org.armacraft.mod.wrapper.CommonGunInfoWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Optional;

public class PlayerNoGunIntegrityEvent extends PlayerEvent {
    private ClientGunInfoWrapper clientInfos;
    private Optional<CommonGunInfoWrapper> serverInfos;
    private static final HandlerList handlers = new HandlerList();

    public PlayerNoGunIntegrityEvent(Player who, ClientGunInfoWrapper clientInfos, Optional<CommonGunInfoWrapper> serverInfos) {
        super(who);
        this.clientInfos = clientInfos;
        this.serverInfos = serverInfos;
    }

    public ClientGunInfoWrapper getClientGunInfos() {
        return clientInfos;
    }

    public Optional<CommonGunInfoWrapper> getCommonInfos() {
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
