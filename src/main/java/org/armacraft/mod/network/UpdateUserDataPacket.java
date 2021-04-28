package org.armacraft.mod.network;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.bridge.bukkit.IUserData;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateUserDataPacket {
    private IUserData userData;

    public UpdateUserDataPacket(IUserData userData) {
        this.userData = userData;
    }

    public static void encode(UpdateUserDataPacket msg, PacketBuffer out) {
        out.writeByte(msg.userData.getFlags().size());
        out.writeByte(msg.userData.getNametagWhitelist().size());
        msg.userData.getFlags().stream().map(IUserData.Flags::toString).forEach(out::writeUtf);
        msg.userData.getNametagWhitelist().forEach(out::writeUtf);
    }

    public static UpdateUserDataPacket decode(PacketBuffer in) {
        Set<IUserData.Flags> flags = new HashSet<>();
        Set<String> nametagWhitelist = new HashSet<>();
        byte flagAmount = in.readByte();
        byte whitelistAmount = in.readByte();

        for (int i = 0; i < flagAmount; i++) {
            flags.add(IUserData.Flags.valueOf(in.readUtf(64)));
        }

        for (int i = 0; i < whitelistAmount; i++) {
            nametagWhitelist.add(in.readUtf(64));
        }

        return new UpdateUserDataPacket(IUserData.of(Minecraft.getInstance().player.getUUID(), flags, nametagWhitelist));
    }

    public static boolean handle(UpdateUserDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> ArmaCraft.getInstance().getClientDist().get().setUserData(msg.userData));
        }
        return true;
    }

}
