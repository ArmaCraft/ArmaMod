package org.armacraft.mod.server;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.event.GunEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.ArmaDist;
import org.armacraft.mod.bridge.bukkit.IBukkitPermissionBridge;
import org.armacraft.mod.bridge.bukkit.IBukkitWorldGuardBridge;
import org.armacraft.mod.network.ClientInfoRequestPacket;
import org.armacraft.mod.network.MACAddressRequestPacket;
import org.armacraft.mod.network.dto.FileInfoDTO;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;
import org.armacraft.mod.server.bukkit.util.BukkitToForgeInterface;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterface;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterfaceImpl;
import org.armacraft.mod.util.FileUtil;
import org.armacraft.mod.wrapper.EnvironmentWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServerDist implements ArmaDist {

	private List<String> extraHashes = new ArrayList<>();
	private List<String> mandatoryHashes = new ArrayList<>();
	private Map<UUID, Long> lastClientInfoRequest = new HashMap<>();

	//private List<String> validHashes = new ArrayList<>();

	//Pontes entre o mod e o Bukkit que são injsetadas pelo server
	public static IBukkitPermissionBridge PERMISSION_BRIDGE;
	public static IBukkitWorldGuardBridge WORLD_GUARD_BRIDGE;

	private final int CLIENT_INFO_REQUEST_DELAY_MILLIS = 10000;
	public static final int GUN_UPDATE_TOLERANCE_MILLIS = 5000;

	//Releva caso alguma arma não tenha integridade enquanto o server atualiza
	//as informações das armas no server para todos os players
	private long gunUpdateBeginMillis = 0;

	public ServerDist() {
		// @StringObfuscator:on
		MinecraftForge.EVENT_BUS.register(this);
		
		File extraFilesFolder = new File("./armacraft/other-allowed-files");
		// Cria a pasta se já não existir
		extraFilesFolder.mkdirs();
		
		File mandatoryFilesFolder = new File("./armacraft/mandatory-files");

		
		// Cria a pasta se já não existir
		mandatoryFilesFolder.mkdirs();
		
		for (File f : extraFilesFolder.listFiles()) {
			this.extraHashes.add(FileUtil.getHash(f));
		}
		
		for (File f : mandatoryFilesFolder.listFiles()) {
			this.mandatoryHashes.add(FileUtil.getHash(f));
		}
		// @StringObfuscator:off
	}

	public void updateGunsForEveryone() {
		this.gunUpdateBeginMillis = System.currentTimeMillis();
		ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(CustomGunDataController.INSTANCE::resendGunData);
	}

	@SubscribeEvent
	public void onBulletHit(GunEvent.HitEntity event) {
		Entity entity = event.getLiving().getEntity();
		entity.getCapability(ModCapabilities.LIVING).ifPresent(living -> {
			PlayerEntity who = (PlayerEntity) living.getEntity();
			Entity target = event.getTarget();
			if(target instanceof PlayerEntity) {
				float damage = event.getDamage();
				boolean hs = event.isHeadshot();
				ForgeToBukkitInterfaceImpl.INSTANCE.onBulletEntityHit(who, (PlayerEntity) target, damage, hs);
			}
		});
	}
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(player -> {
			if(lastClientInfoRequest.get(player.getUUID()) != null &&
					lastClientInfoRequest.get(player.getUUID()) - System.currentTimeMillis() >= CLIENT_INFO_REQUEST_DELAY_MILLIS) {
				this.requestClientInfo(player);
				lastClientInfoRequest.put(player.getUUID(), System.currentTimeMillis());
			}
		});
	}

	@SubscribeEvent
	public void onLoggedIn(PlayerLoggedInEvent event) {
		this.requestClientInfo(event.getPlayer());
		CustomGunDataController.INSTANCE.resendGunData(event.getPlayer());
		this.lastClientInfoRequest.put(event.getPlayer().getUUID(), System.currentTimeMillis());
		ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()),
				new MACAddressRequestPacket());
	}

	@SubscribeEvent
	public void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		this.lastClientInfoRequest.remove(event.getPlayer().getUUID());
	}

	@Override
	public boolean validateClassesHash(String hash, PlayerEntity source) {
		/*if(!validHashes.contains(hash)) {
			this.getForgeToBukkitInterface().onNoClassesIntegrity(source, hash, validHashes);
			return false;
		}*/
		return true;
	}

	@Override
	public void validateUntrustedFolders(List<FolderSnapshotDTO> snapshots, PlayerEntity source) {
		for (FolderSnapshotDTO snapshot : snapshots) {

			if(snapshot.getGameFolder().getFolderPath().equals("./mods")) {
				// Arquivos obrigatórios
				List<String> missingMandatory = this.mandatoryHashes.stream().filter(mandatory -> !snapshot.getAllHashes().contains(mandatory)).collect(Collectors.toList());

				// Falta arquivo
				if (!missingMandatory.isEmpty()) {
					this.getForgeToBukkitInterface().onMissingFile(source, missingMandatory);
				}
			}
			
			List<FileInfoDTO> unknownFiles = snapshot.getFiles().stream().filter(fileInfo -> {
				// Não está registrado em lugar algum
				return !this.extraHashes.contains(fileInfo.getFileHash()) && !this.mandatoryHashes.contains(fileInfo.getFileHash());
			}).collect(Collectors.toList());
			
			if (!unknownFiles.isEmpty()) {
				this.getForgeToBukkitInterface().onUnknownFile(source, unknownFiles);
			}
		}
	}

	@Override
	public void validateTransformationServices(List<String> transformationServices, PlayerEntity source) {
		this.getForgeToBukkitInterface().onTransformationServicesReceive(source, transformationServices);
	}
	
	public Optional<ServerPlayerEntity> getOnlinePlayerByUUID(UUID uuid) {
		return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid));
	}
	
	public ForgeToBukkitInterface getForgeToBukkitInterface() {
		return ForgeToBukkitInterfaceImpl.INSTANCE;
	}
	
	public static ServerDist get() {
		return ArmaCraft.getInstance().getServerDist().get();
	}

	private void requestClientInfo(PlayerEntity player) {

	}

	@Override
	public EnvironmentWrapper getEnvironment() {
		return null;
	}

	public List<String> getExtraHashes() {
		return extraHashes;
	}

	public List<String> getMandatoryHashes() {
		return mandatoryHashes;
	}

	public Map<UUID, Long> getLastClientInfoRequest() {
		return lastClientInfoRequest;
	}

	public long getGunUpdateBeginMillis() {
		return gunUpdateBeginMillis;
	}
}
