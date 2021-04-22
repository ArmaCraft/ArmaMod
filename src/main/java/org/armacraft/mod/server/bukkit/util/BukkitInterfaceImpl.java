package org.armacraft.mod.server.bukkit.util;

import java.util.List;

import io.izzel.arclight.common.bridge.entity.player.ServerPlayerEntityBridge;
import org.armacraft.mod.environment.EnvironmentWrapper;
import org.armacraft.mod.network.dto.FileInfoDTO;
import org.armacraft.mod.server.bukkit.event.PlayerDashEvent;
import org.armacraft.mod.server.bukkit.event.PlayerMissingFilesEvent;
import org.armacraft.mod.server.bukkit.event.PlayerSentEnvironmentEvent;
import org.armacraft.mod.server.bukkit.event.PlayerSentUnknownFilesEvent;
import org.armacraft.mod.server.bukkit.event.PlayerTransformationServiceReceiveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
public enum BukkitInterfaceImpl implements BukkitInterface {
	INSTANCE;
	
	public void onDash(PlayerEntity entity) {
		Bukkit.getPluginManager().callEvent(new PlayerDashEvent(this.getBukkitPlayer(entity)));
	}

	@Override
	public void onEnvironmentReceive(PlayerEntity who, EnvironmentWrapper environmentWrapper) {
		Bukkit.getPluginManager().callEvent(new PlayerSentEnvironmentEvent(this.getBukkitPlayer(who), environmentWrapper));
	}

	public Player getBukkitPlayer(PlayerEntity playerEntity) {
		return ((ServerPlayerEntityBridge) playerEntity).bridge$getBukkitEntity();
	}

	@Override
	public void onMissingFile(PlayerEntity entity, List<String> missingHashes) {
		Bukkit.getPluginManager().callEvent(new PlayerMissingFilesEvent(this.getBukkitPlayer(entity), missingHashes));
	}

	@Override
	public void onUnknownFile(PlayerEntity entity, List<FileInfoDTO> unknownFiles) {
		Bukkit.getPluginManager().callEvent(new PlayerSentUnknownFilesEvent(this.getBukkitPlayer(entity), unknownFiles));
	}

	@Override
	public void onTransformationServicesReceive(PlayerEntity entity, List<String> transformationServices) {
		Bukkit.getPluginManager().callEvent(new PlayerTransformationServiceReceiveEvent(this.getBukkitPlayer(entity), transformationServices));
	}
}
