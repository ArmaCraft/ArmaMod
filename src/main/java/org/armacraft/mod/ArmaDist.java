package org.armacraft.mod;

import java.util.List;

import org.armacraft.mod.network.dto.FolderSnapshotDTO;

import net.minecraft.entity.player.PlayerEntity;

public interface ArmaDist {
	boolean validateClassesHash(String hash, PlayerEntity source);
	void validateUntrustedFolders(List<FolderSnapshotDTO> snapshots, PlayerEntity source);
	void validateTransformationServices(List<String> transformationServices, PlayerEntity source);
}