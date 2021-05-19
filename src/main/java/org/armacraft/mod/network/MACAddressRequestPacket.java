package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.armacraft.mod.ArmaCraft;

import java.util.function.Supplier;

public class MACAddressRequestPacket {
    public static void encode(MACAddressRequestPacket msg, PacketBuffer out) {}

    public static MACAddressRequestPacket decode(PacketBuffer in) {
        return new MACAddressRequestPacket();
    }

    public static boolean handle(MACAddressRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                ArmaCraft.networkChannel.send(PacketDistributor.SERVER.noArg(), new MACAddressResponsePacket(null));
            });
        }
        return true;
    }
}
