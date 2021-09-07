package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.bridge.bukkit.IUserData;
import org.armacraft.mod.client.ClientUserData;
import org.armacraft.mod.wrapper.KeyBindWrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class UpdateUserDataPacket {
    private IUserData userData;

    public UpdateUserDataPacket(IUserData userData) {
        this.userData = userData;
    }

    public static void encode(UpdateUserDataPacket msg, PacketBuffer out) {
        out.writeBoolean(msg.userData.areKeybindsEnabled());
        out.writeByte(msg.userData.getFlags().size());
        out.writeByte(msg.userData.getNametagWhitelist().size());
        msg.userData.getFlags().stream().map(IUserData.Flags::toString).forEach(out::writeUtf);
        msg.userData.getNametagWhitelist().forEach(out::writeUtf);
    }

    public static UpdateUserDataPacket decode(PacketBuffer in) {
        Set<IUserData.Flags> flags = new HashSet<>();
        Set<String> nametagWhitelist = new HashSet<>();
        boolean areKeybindsEnabled = in.readBoolean();
        byte flagAmount = in.readByte();
        byte whitelistAmount = in.readByte();

        for (int i = 0; i < flagAmount; i++) {
            String flag = in.readUtf(64);
            flags.add(IUserData.Flags.valueOf(flag));
        }

        for (int i = 0; i < whitelistAmount; i++) {
            nametagWhitelist.add(in.readUtf(64));
        }

        //TODO: Implementar opção de renderizar roupas
        return new UpdateUserDataPacket(new ClientUserData(flags, nametagWhitelist, areKeybindsEnabled, true));
    }

    public static boolean handle(UpdateUserDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> ArmaCraft.getInstance().getClientDist().get().setUserData(msg.userData));
        }
        return true;
    }

}
