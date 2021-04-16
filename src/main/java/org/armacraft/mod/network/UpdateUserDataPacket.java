package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.client.ClientUserData;

import java.util.HashSet;
import java.util.function.Supplier;

public class UpdateUserDataPacket {
    private ClientUserData userData;

    public UpdateUserDataPacket(ClientUserData userData) {
        this.userData = userData;
    }

    public static void encode(UpdateUserDataPacket msg, PacketBuffer out) {
        out.writeByte(msg.userData.getFlags().size());
        out.writeByte(msg.userData.getNametagWhitelist().size());
        msg.userData.getFlags().forEach(flag -> out.writeByteArray(flag.getBytes()));
        msg.userData.getNametagWhitelist().forEach(nametag -> out.writeByteArray(nametag.getBytes()));
    }

    public static UpdateUserDataPacket decode(PacketBuffer in) {
        UpdateUserDataPacket packet = UpdateUserDataPacket.empty();

        byte flagAmount = in.readByte();
        byte whitelistAmount = in.readByte();

        for (int i = 0; i < flagAmount; i++) {
            packet.userData.getFlags().add(in.readUtf());
        }

        for (int i = 0; i < whitelistAmount; i++) {
            packet.userData.getNametagWhitelist().add(in.readUtf());
        }

        return packet;
    }

    public static boolean handle(UpdateUserDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> ArmaCraft.getInstance().getClientDist().setClientUserData(msg.userData));
        }
        return true;
    }

    public static UpdateUserDataPacket empty() {
        return new UpdateUserDataPacket(new ClientUserData(new HashSet<>(), new HashSet<>()));
    }
}
