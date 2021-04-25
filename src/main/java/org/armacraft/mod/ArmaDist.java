package org.armacraft.mod;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.armacraft.mod.environment.EnvironmentWrapper;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;

import net.minecraft.entity.player.PlayerEntity;

public interface ArmaDist {
	default boolean isJavaInDebugMode() {
		return ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}
	EnvironmentWrapper getEnvironment();
	boolean validateClassesHash(String hash, PlayerEntity source);
	void validateUntrustedFolders(List<FolderSnapshotDTO> snapshots, PlayerEntity source);
	void validateTransformationServices(List<String> transformationServices, PlayerEntity source);
}