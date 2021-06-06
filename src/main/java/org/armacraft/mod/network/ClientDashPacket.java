package org.armacraft.mod.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.util.Cooldown;

import java.util.function.Supplier;

public class ClientDashPacket {
	
	public static void encode(ClientDashPacket msg, PacketBuffer out) {}

	public static ClientDashPacket decode(PacketBuffer in) {
		return new ClientDashPacket();
	}

	public static boolean handle(ClientDashPacket msg, Supplier<NetworkEvent.Context> ctx) {
		//StringObfuscation: on
		if (ctx.get().getDirection().getReceptionSide().isServer()) {
			ctx.get().enqueueWork(() -> {
				ServerPlayerEntity player = ctx.get().getSender();
				if (!Cooldown.checkAndPut(player, "dashpacket", 350)) {
					ArmaCraft.getInstance().getServerDist().ifPresent(dist -> {
						dist.getForgeToBukkitInterface().onDash(ctx.get().getSender());
					});
				}
			});
		}
		//StringObfuscation: off
		return true;
	}
}
