package org.armacraft.mod.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.ArmaDist;
import org.armacraft.mod.client.ClientUserData;
import org.armacraft.mod.network.ClientInfoRequestPacket;
import org.armacraft.mod.network.UpdateUserDataPacket;
import org.armacraft.mod.network.dto.FileInfoDTO;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;
import org.armacraft.mod.server.bukkit.util.BukkitInterface;
import org.armacraft.mod.server.bukkit.util.BukkitInterfaceImpl;

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
		
		File extraFilesFolder = new File("./armacraft/other-allowed-files");
		// Cria a pasta se já não existir
		extraFilesFolder.mkdirs();
		
		File mandatoryFilesFolder = new File("./armacraft/mandatory-files");
		// Cria a pasta se já não existir
		mandatoryFilesFolder.mkdirs();
		
		for (File f : extraFilesFolder.listFiles()) {
			this.extraHashes.add(FolderSnapshotDTO.getHash(f));
		}
		
		for (File f : mandatoryFilesFolder.listFiles()) {
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
		// Arquivos obrigatórios
		List<String> missingMandatory = this.mandatoryHashes.stream().filter(mandatoryHash -> !snapshot.getAllHashes().contains(mandatoryHash)).collect(Collectors.toList());
		
		// Falta arquivo
		if (!missingMandatory.isEmpty()) {
			this.getBukkitInterface().onMissingFile(source, missingMandatory);
		}
		
		List<FileInfoDTO> unknownFiles = snapshot.getFiles().stream().filter(fileInfo -> {
			// Não está registrado em lugar algum
			return !this.extraHashes.contains(fileInfo.getFileHash()) && !this.mandatoryHashes.contains(fileInfo.getFileHash());
		}).collect(Collectors.toList());
		
		if (!unknownFiles.isEmpty()) {
			this.getBukkitInterface().onUnknownFile(source, unknownFiles);
		}
	}

	@Override
	public void validateTransformationServices(List<String> transformationServices, PlayerEntity source) {
		this.getBukkitInterface().onTransformationServicesReceive(source, transformationServices);
	}
	
	public BukkitInterface getBukkitInterface() {
		return BukkitInterfaceImpl.INSTANCE;
	}
}
