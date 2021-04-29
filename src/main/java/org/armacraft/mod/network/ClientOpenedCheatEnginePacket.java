package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterfaceImpl;

import java.util.function.Supplier;

public class ClientOpenedCheatEnginePacket {

    public static void encode(ClientOpenedCheatEnginePacket msg, PacketBuffer out) {}
    public static ClientOpenedCheatEnginePacket decode(PacketBuffer in) { return new ClientOpenedCheatEnginePacket(); }

    public static boolean handle(ClientOpenedCheatEnginePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isServer()) {
            return true;
        }

        ForgeToBukkitInterfaceImpl.INSTANCE.onCheatEngineOpened(ctx.get().getSender());

        return true;
    }
}
