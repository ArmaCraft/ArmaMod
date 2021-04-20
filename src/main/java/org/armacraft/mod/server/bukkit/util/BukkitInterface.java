package org.armacraft.mod.server.bukkit.util;

import java.util.List;

import org.armacraft.mod.network.dto.FileInfoDTO;
import org.bukkit.entity.Player;

import net.minecraft.entity.player.PlayerEntity;

public interface BukkitInterface {
	void onDash(PlayerEntity entity);
	void onMissingFile(PlayerEntity entity, List<String> missingHashes);
	void onUnknownFile(PlayerEntity entity, List<FileInfoDTO> unknownFiles);
	void onTransformationServicesReceive(PlayerEntity entity, List<String> transformationServices);
	Player getBukkitPlayer(PlayerEntity playerEntity);
}
