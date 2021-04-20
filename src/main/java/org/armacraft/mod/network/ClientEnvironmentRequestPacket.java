package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.armacraft.mod.ArmaCraft;

import java.util.function.Supplier;

public class ClientEnvironmentRequestPacket {
    public static void encode(ClientEnvironmentRequestPacket msg, PacketBuffer out) {}

    public static ClientEnvironmentRequestPacket decode(PacketBuffer in) {
        return new ClientEnvironmentRequestPacket();
    }

    public static boolean handle(ClientEnvironmentRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                ArmaCraft.networkChannel.send(PacketDistributor.SERVER.noArg(), ClientEnvironmentResponsePacket.empty());
            });
        }
        return true;
    }
}
