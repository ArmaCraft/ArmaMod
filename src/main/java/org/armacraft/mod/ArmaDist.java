package org.armacraft.mod;

import java.util.List;

import org.armacraft.mod.network.dto.FolderSnapshotDTO;

import net.minecraft.entity.player.PlayerEntity;

public interface ArmaDist {
	void validateUntrustedFolder(FolderSnapshotDTO snapshot, PlayerEntity source);
	void validateTransformationServices(List<String> transformationServices, PlayerEntity source);
}