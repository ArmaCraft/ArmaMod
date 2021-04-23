package org.armacraft.mod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.armacraft.mod.ArmaCraft;

import java.util.function.Supplier;

public class CloseGamePacket {
    public static void encode(CloseGamePacket msg, PacketBuffer out) {}

    public static CloseGamePacket decode(PacketBuffer in) {
        return new CloseGamePacket();
    }

    public static boolean handle(CloseGamePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> Minecraft.getInstance().stop());
        }
        return true;
    }
}
