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
        out.writeByte(msg.userData.getKeyBinds().size());
        msg.userData.getFlags().stream().map(IUserData.Flags::toString).forEach(out::writeUtf);
        msg.userData.getNametagWhitelist().forEach(out::writeUtf);
        msg.userData.getKeyBinds().stream().map(KeyBindWrapper::toString).forEach(out::writeUtf);
    }

    public static UpdateUserDataPacket decode(PacketBuffer in) {
        Set<IUserData.Flags> flags = new HashSet<>();
        Set<String> nametagWhitelist = new HashSet<>();
        Set<KeyBindWrapper> keybinds = new HashSet<>();
        boolean areKeybindsEnabled = in.readBoolean();
        byte flagAmount = in.readByte();
        byte whitelistAmount = in.readByte();
        byte bindsAmount = in.readByte();

        for (int i = 0; i < flagAmount; i++) {
            flags.add(IUserData.Flags.valueOf(in.readUtf(64)));
        }

        for (int i = 0; i < whitelistAmount; i++) {
            nametagWhitelist.add(in.readUtf(64));
        }

        for (int i = 0; i < bindsAmount; i++) {
            keybinds.add(KeyBindWrapper.fromString(in.readUtf(64)));
        }

        return new UpdateUserDataPacket(new ClientUserData(keybinds, flags, nametagWhitelist, areKeybindsEnabled));
    }

    public static boolean handle(UpdateUserDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> ArmaCraft.getInstance().getClientDist().get().setUserData(msg.userData));
        }
        return true;
    }

}
