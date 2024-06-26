package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.armacraft.mod.ArmaCraft;

import java.util.function.Supplier;

public class ClientClassesHashRequestPacket {

    public static void encode(ClientClassesHashRequestPacket msg, PacketBuffer in) {}

    public static ClientClassesHashRequestPacket decode(PacketBuffer in) {
        return new ClientClassesHashRequestPacket();
    }

    public static boolean handle(ClientClassesHashRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                ArmaCraft.networkChannel.send(PacketDistributor.SERVER.noArg(), new ClientClassesHashResponsePacket(""));
            });
        }
        return true;
    }

}
