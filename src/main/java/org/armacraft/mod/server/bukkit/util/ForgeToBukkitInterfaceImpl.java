package org.armacraft.mod.server.bukkit.util;

import io.izzel.arclight.common.bridge.entity.player.ServerPlayerEntityBridge;
import io.izzel.arclight.common.bridge.world.WorldBridge;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.armacraft.mod.bridge.bukkit.IBukkitWorldGuardBridge;
import org.armacraft.mod.bridge.bukkit.IUserData;
import org.armacraft.mod.network.dto.FileInfoDTO;
import org.armacraft.mod.server.ServerDist;
import org.armacraft.mod.server.bukkit.event.MACAddressReceivedEvent;
import org.armacraft.mod.server.bukkit.event.PlayerBulletHitEntityEvent;
import org.armacraft.mod.server.bukkit.event.PlayerDashEvent;
import org.armacraft.mod.server.bukkit.event.PlayerMissingFilesEvent;
import org.armacraft.mod.server.bukkit.event.PlayerNoClassesIntegrityEvent;
import org.armacraft.mod.server.bukkit.event.PlayerNoGunIntegrityEvent;
import org.armacraft.mod.server.bukkit.event.PlayerOpenedCheatEngineEvent;
import org.armacraft.mod.server.bukkit.event.PlayerSentEnvironmentEvent;
import org.armacraft.mod.server.bukkit.event.PlayerSentUnknownFilesEvent;
import org.armacraft.mod.server.bukkit.event.PlayerTransformationServiceReceiveEvent;
import org.armacraft.mod.wrapper.ClientGunDataWrapper;
import org.armacraft.mod.wrapper.CommonGunDataWrapper;
import org.armacraft.mod.wrapper.EnvironmentWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@OnlyIn(Dist.DEDICATED_SERVER)
public enum ForgeToBukkitInterfaceImpl implements ForgeToBukkitInterface {
	INSTANCE;

	@Override
	public void onBulletEntityHit(PlayerEntity entity, PlayerEntity target, float damage, boolean headshot) {
		Bukkit.getPluginManager().callEvent(new PlayerBulletHitEntityEvent(getBukkitPlayer(entity), getBukkitPlayer(target), damage, headshot));
	}

	public void onDash(PlayerEntity entity) {
		Bukkit.getPluginManager().callEvent(new PlayerDashEvent(this.getBukkitPlayer(entity)));
	}

	@Override
	public IUserData retrieveUserData(PlayerEntity holder) {
		return retrieveUserData(holder.getUUID());
	}

	@Override
	public IUserData retrieveUserData(UUID uuid) {
		return null;
	}

	@Override
	public void onCheatEngineOpened(PlayerEntity who) {
		Bukkit.getPluginManager().callEvent(new PlayerOpenedCheatEngineEvent(this.getBukkitPlayer(who)));
	}

	@Override
	public void onMACAddressResponse(PlayerEntity who, byte[] address) {
		Bukkit.getPluginManager().callEvent(new MACAddressReceivedEvent(this.getBukkitPlayer(who), address));
	}

	@Override
	public void onGunNoIntegrity(PlayerEntity player, ClientGunDataWrapper clientInfos, Optional<CommonGunDataWrapper> serverInfos) {
		Bukkit.getPluginManager().callEvent(new PlayerNoGunIntegrityEvent(this.getBukkitPlayer(player), clientInfos, serverInfos));
	}

	@Override
	public void onNoClassesIntegrity(PlayerEntity who, String hash, List<String> expectedHashes) {
		Bukkit.getPluginManager().callEvent(new PlayerNoClassesIntegrityEvent(this.getBukkitPlayer(who), hash, expectedHashes));
	}

	@Override
	public void onEnvironmentReceive(PlayerEntity who, EnvironmentWrapper environmentWrapper) {
		Bukkit.getPluginManager().callEvent(new PlayerSentEnvironmentEvent(this.getBukkitPlayer(who), environmentWrapper));
	}

	public Player getBukkitPlayer(PlayerEntity playerEntity) {
		return ((ServerPlayerEntityBridge) playerEntity).bridge$getBukkitEntity();
	}

	public org.bukkit.World getBukkitWorld(World level) {
		return ((WorldBridge) level).bridge$getWorld();
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

	public boolean isWorldGuardFlagAllowed(String flag, Entity entity) {
		return ServerDist.WORLD_GUARD_BRIDGE.testState(flag, getBukkitWorld(entity.level).getUID(), (int) entity.getX(), (int) entity.getY(), (int) entity.getZ()) == IBukkitWorldGuardBridge.State.ALLOWED;
	}
}

