package org.armacraft.mod.init;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.network.RequestModsPacket;
import org.armacraft.mod.network.UpdateVisibleNametagsPacket;
import org.armacraft.mod.util.MiscUtil;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class ServerDist implements ArmaDist {

	private Map<String, String> serverHashes = MiscUtil.calculateMyHashes();

	public ServerDist() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onLoggedIn(PlayerLoggedInEvent event) {
		ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()),
				new RequestModsPacket());
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if(ArmaCraft.NAMETAG_BRIDGE != null) {
			ArmaCraft.NAMETAG_BRIDGE.getNametagUpdateWatcher().entrySet().stream()
					.filter(Map.Entry::getValue)
					.map(Map.Entry::getKey)
					.forEach(uuid -> {
						Collection<String> visibleTags = ArmaCraft.NAMETAG_BRIDGE.getNametagVisibility().get(uuid);
						ServerPlayerEntity entity = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
						if (entity != null) {
							ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> entity),
									new UpdateVisibleNametagsPacket(new HashSet<>(visibleTags)));
						}
					});
			ArmaCraft.NAMETAG_BRIDGE.getNametagUpdateWatcher().clear();
		}
	}

	public Map<String, String> getHashes() {
		return this.serverHashes;
	}
}
