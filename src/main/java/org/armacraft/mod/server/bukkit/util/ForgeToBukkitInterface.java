package org.armacraft.mod.server.bukkit.util;

import java.util.List;

import org.armacraft.mod.wrapper.EnvironmentWrapper;
import org.armacraft.mod.network.dto.FileInfoDTO;
import org.armacraft.mod.wrapper.GunInfoWrapper;
import org.bukkit.entity.Player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
public interface ForgeToBukkitInterface {
	void onDash(PlayerEntity entity);
	void onCheatEngineOpened(PlayerEntity who);
	void onGunNoIntegrity(PlayerEntity player, GunInfoWrapper infos);
	void onNoClassesIntegrity(PlayerEntity who, String hash, List<String> expectedHashes);
	void onEnvironmentReceive(PlayerEntity who, EnvironmentWrapper environmentWrapper);
	void onMissingFile(PlayerEntity entity, List<String> missingHashes);
	void onUnknownFile(PlayerEntity entity, List<FileInfoDTO> unknownFiles);
	void onTransformationServicesReceive(PlayerEntity entity, List<String> transformationServices);
	Player getBukkitPlayer(PlayerEntity playerEntity);
	
	static ForgeToBukkitInterface getInstance() {
		return ForgeToBukkitInterfaceImpl.INSTANCE;
	}
}
