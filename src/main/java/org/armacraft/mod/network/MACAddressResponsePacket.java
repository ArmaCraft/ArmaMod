package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterfaceImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.UUID;
import java.util.function.Supplier;

public class MACAddressResponsePacket {
    private byte[] bytes;

    public MACAddressResponsePacket(byte[] bytes) {
        this.bytes = bytes;
    }

    public static void encode(MACAddressResponsePacket msg, PacketBuffer out) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            out.writeByteArray(ni.getHardwareAddress());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static MACAddressResponsePacket decode(PacketBuffer in) {
        return new MACAddressResponsePacket(in.readByteArray());
    }

    public static boolean handle(MACAddressResponsePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isClient()) {
            ForgeToBukkitInterfaceImpl.INSTANCE.onMACAddressResponse(ctx.get().getSender(), msg.bytes);
        }
        return true;
    }
}
