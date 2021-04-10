package org.armacraft.mod.init;

import java.util.Map;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.network.RequestModsPacket;
import org.armacraft.mod.util.ModsUtil;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class ServerDist implements ArmaDist {

	private Map<String, String> serverHashes = ModsUtil.calculateMyHashes();
	
	public ServerDist() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
    @SubscribeEvent
    public void onLoggedIn(PlayerLoggedInEvent event) {
    	// Pede pro player enviar os mods dele
    	ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new RequestModsPacket());
    }
	
    public Map<String, String> getHashes() {
    	return this.serverHashes;
    }
}
