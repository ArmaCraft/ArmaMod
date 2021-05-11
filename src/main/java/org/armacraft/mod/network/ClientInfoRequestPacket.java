package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.armacraft.mod.ArmaCraft;

import java.util.function.Supplier;

public class ClientInfoRequestPacket {

	public static void encode(ClientInfoRequestPacket msg, PacketBuffer out) {}

	public static ClientInfoRequestPacket decode(PacketBuffer in) {
		return new ClientInfoRequestPacket();
	}

	public static boolean handle(ClientInfoRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isClient()) {
			ctx.get().enqueueWork(() -> {
				// Envia meus mods ao server
				ArmaCraft.networkChannel.send(PacketDistributor.SERVER.noArg(), ClientInfoResponsePacket.withMyMods());
			});
		}
		return true;
	}
}
