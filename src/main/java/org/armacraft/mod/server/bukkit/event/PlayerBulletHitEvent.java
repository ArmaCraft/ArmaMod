package org.armacraft.mod.server.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;

public class PlayerBulletHitEvent extends PlayerEvent {

    private Player target;
    private float damage;
    private boolean headshot;

    private static final HandlerList handlers = new HandlerList();

    public PlayerBulletHitEvent(Player who, Player target, float damage, boolean headshot) {
        super(who);
        this.target = target;
        this.damage = damage;
        this.headshot = headshot;
    }

    public Player getTarget() {
        return target;
    }

    public float getDamage() {
        return damage;
    }

    public boolean isHeadshot() {
        return headshot;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
