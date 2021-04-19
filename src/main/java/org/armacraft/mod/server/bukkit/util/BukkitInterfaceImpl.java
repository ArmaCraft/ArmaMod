package org.armacraft.mod.server.bukkit.util;

import org.armacraft.mod.server.bukkit.event.PlayerDashEvent;
import org.armacraft.mod.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minecraft.entity.player.PlayerEntity;

public enum BukkitInterfaceImpl implements BukkitInterface {
	INSTANCE;
	
	public void onDash(PlayerEntity entity) {
		Bukkit.getPluginManager().callEvent(new PlayerDashEvent(entity));
	}
	
	public Player getBukkitPlayer(PlayerEntity playerEntity) {
		return Bukkit.getPlayerExact(MiscUtil.getPlayerName(playerEntity));
	}
}
