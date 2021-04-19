package org.armacraft.mod;

import org.armacraft.mod.network.dto.FolderSnapshotDTO;

import net.minecraft.entity.player.PlayerEntity;

public interface ArmaDist {
	void validateUntrustedFolder(FolderSnapshotDTO snapshot, PlayerEntity source);
}