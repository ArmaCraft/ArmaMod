package org.armacraft.mod.server.bukkit.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.armacraft.mod.bridge.bukkit.IUserData;
import org.armacraft.mod.network.dto.FileInfoDTO;
import org.armacraft.mod.wrapper.ClientGunDataWrapper;
import org.armacraft.mod.wrapper.CommonGunDataWrapper;
import org.armacraft.mod.wrapper.EnvironmentWrapper;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@OnlyIn(Dist.DEDICATED_SERVER)
public interface ForgeToBukkitInterface {
	void onBulletHit(PlayerEntity entity, PlayerEntity target, float damage, boolean headshot);
	void onDash(PlayerEntity entity);
	IUserData retrieveUserData(UUID holder);
	IUserData retrieveUserData(PlayerEntity holder);
	void onCheatEngineOpened(PlayerEntity who);
	void onGunNoIntegrity(PlayerEntity who, ClientGunDataWrapper clientInfos, Optional<CommonGunDataWrapper> commonInfos);
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
