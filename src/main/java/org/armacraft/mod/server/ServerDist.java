package org.armacraft.mod.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.ArmaDist;
import org.armacraft.mod.client.ClientUserData;
import org.armacraft.mod.network.ClientInfoRequestPacket;
import org.armacraft.mod.network.UpdateUserDataPacket;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;
import org.armacraft.mod.util.MiscUtil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ServerDist implements ArmaDist {
	
	private List<String> extraHashes = new ArrayList<>();
	private List<String> mandatoryHashes = new ArrayList<>();

	public ServerDist() {
		MinecraftForge.EVENT_BUS.register(this);
		
		File extraModsFolder = new File("./armacraft/other-allowed-mods");
		// Cria a pasta se já não existir
		extraModsFolder.mkdirs();
		
		File mandatoryModsFolder = new File("./armacraft/mandatory-mods");
		// Cria a pasta se já não existir
		mandatoryModsFolder.mkdirs();
		
		for (File f : extraModsFolder.listFiles()) {
			this.extraHashes.add(FolderSnapshotDTO.getHash(f));
		}
		
		for (File f : mandatoryModsFolder.listFiles()) {
			this.mandatoryHashes.add(FolderSnapshotDTO.getHash(f));
		}
	}
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if(ArmaCraft.USER_DATA_CONTROLLER != null) {
			ArmaCraft.USER_DATA_CONTROLLER.getUserDataUpdateWatcher().entrySet().stream()
					.filter(Map.Entry::getValue)
					.map(Map.Entry::getKey)
					.forEach(uuid -> {
						ServerPlayerEntity entity = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
						ArmaCraft.USER_DATA_CONTROLLER.getUsersData().stream()
								.filter(user -> user.getHolder().equals(uuid)).findFirst().ifPresent(data -> {
							if (entity != null) {
								ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> entity),
										new UpdateUserDataPacket(ClientUserData.from(data)));
							}
						});
					});
			ArmaCraft.USER_DATA_CONTROLLER.getUserDataUpdateWatcher().clear();
		}
	}


	@SubscribeEvent
	public void onLoggedIn(PlayerLoggedInEvent event) {
		ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()),
				new ClientInfoRequestPacket());
	}

	@Override
	public void validateUntrustedFolder(FolderSnapshotDTO snapshot, PlayerEntity source) {
		final String playerName = MiscUtil.getPlayerName(source);
		
		// Se falta arquivo
		this.mandatoryHashes.forEach(mandatoryHash -> {
			if (!snapshot.getFileHashes().contains(mandatoryHash)) {
				MiscUtil.runConsoleCommand("delegategameinfo missinghash " + playerName + " "
						+ mandatoryHash);
			}
		});
		
		snapshot.getFileHashesMap().entrySet().stream().forEach(entry -> {
			// Não está registrado em lugar algum
			if (!this.extraHashes.contains(entry.getKey()) && !this.mandatoryHashes.contains(entry.getKey())) {
				MiscUtil.runConsoleCommand("delegategameinfo unknownhash " + playerName + " "
						+ entry.getKey() + " " + entry.getValue());
			}
		});
	}
}
