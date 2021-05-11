package org.armacraft.mod;

import net.minecraft.entity.player.PlayerEntity;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;
import org.armacraft.mod.wrapper.EnvironmentWrapper;

import java.lang.management.ManagementFactory;
import java.util.List;

public interface ArmaDist {
	default boolean isJavaInDebugMode() {
		return ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}
	EnvironmentWrapper getEnvironment();
	boolean validateClassesHash(String hash, PlayerEntity source);
	void validateUntrustedFolders(List<FolderSnapshotDTO> snapshots, PlayerEntity source);
	void validateTransformationServices(List<String> transformationServices, PlayerEntity source);
}