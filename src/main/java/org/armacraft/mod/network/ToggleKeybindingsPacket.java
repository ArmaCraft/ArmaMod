package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.ArmaCraft;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ToggleKeybindingsPacket {
    private boolean enabled;

    public ToggleKeybindingsPacket(boolean enabled) {
        this.enabled = enabled;
    }

    public static void encode(ToggleKeybindingsPacket msg, PacketBuffer out) {
        out.writeBoolean(msg.enabled);
    }

    public static ToggleKeybindingsPacket decode(PacketBuffer in) {
        return new ToggleKeybindingsPacket(in.readBoolean());
    }

    public static boolean handle(ToggleKeybindingsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> ArmaCraft.getInstance().getClientDist().get().getUserData().toggleKeybindings(msg.enabled));
        }
        return true;
    }

}
