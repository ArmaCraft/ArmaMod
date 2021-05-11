package org.armacraft.mod.server.bukkit.event;

import org.armacraft.mod.wrapper.ClientGunDataWrapper;
import org.armacraft.mod.wrapper.CommonGunDataWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Optional;

public class PlayerNoGunIntegrityEvent extends PlayerEvent {
    private ClientGunDataWrapper clientInfos;
    private Optional<CommonGunDataWrapper> serverInfos;
    private static final HandlerList handlers = new HandlerList();

    public PlayerNoGunIntegrityEvent(Player who, ClientGunDataWrapper clientInfos, Optional<CommonGunDataWrapper> serverInfos) {
        super(who);
        this.clientInfos = clientInfos;
        this.serverInfos = serverInfos;
    }

    public ClientGunDataWrapper getClientGunInfos() {
        return clientInfos;
    }

    public Optional<CommonGunDataWrapper> getCommonInfos() {
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
