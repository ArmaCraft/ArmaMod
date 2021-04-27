package org.armacraft.mod.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterfaceImpl;
import org.armacraft.mod.util.GunUtils;
import org.armacraft.mod.wrapper.GunInfoWrapper;

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
