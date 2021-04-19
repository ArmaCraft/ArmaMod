package org.armacraft.mod.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minecraft.entity.player.PlayerEntity;

public class BukkitUtils {

	public static Player getBukkitPlayer(PlayerEntity playerEntity) {
		return Bukkit.getPlayerExact(MiscUtil.getPlayerName(playerEntity));
	}
}
