package org.armacraft.mod.network;

import java.util.function.Supplier;

import org.armacraft.mod.ArmaCraft;

import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class RequestModsPacket {

	public static boolean handle(ResponseModsPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isClient()) {
			ctx.get().enqueueWork(() -> {
				// Envia meus mods ao server
				ArmaCraft.networkChannel.send(PacketDistributor.SERVER.noArg(), ResponseModsPacket.withMyMods());
			});
		}
		return true;
	}
}
