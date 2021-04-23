package org.armacraft.mod.network;

import java.util.function.Supplier;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.util.Cooldown;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientDashPacket {
	
	public static void encode(ClientDashPacket msg, PacketBuffer out) {}

	public static ClientDashPacket decode(PacketBuffer in) {
		return new ClientDashPacket();
	}

	public static boolean handle(ClientDashPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isServer()) {
			ctx.get().enqueueWork(() -> {
				if (!Cooldown.checkAndPut(ctx.get().getSender(), "dashpacket", 350)) {
					ArmaCraft.getInstance().getServerDist().ifPresent(dist -> {
						dist.getForgeToBukkitInterface().onDash(ctx.get().getSender());
					});
				}
			});
		}
		return true;
	}
}
