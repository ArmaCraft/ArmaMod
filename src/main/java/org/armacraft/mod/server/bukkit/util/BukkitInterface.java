package org.armacraft.mod.server.bukkit.util;

import org.bukkit.entity.Player;

import net.minecraft.entity.player.PlayerEntity;

public interface BukkitInterface {
	void onDash(PlayerEntity entity);
	Player getBukkitPlayer(PlayerEntity playerEntity);
}
