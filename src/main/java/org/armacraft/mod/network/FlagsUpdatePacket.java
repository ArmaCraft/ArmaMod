package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.bridge.bukkit.IUserData;
import org.armacraft.mod.wrapper.KeyBindWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class FlagsUpdatePacket {
    private Set<IUserData.Flags> flags;

    public FlagsUpdatePacket(Set<IUserData.Flags> flags) {
        this.flags = flags;
    }

    public static void encode(FlagsUpdatePacket msg, PacketBuffer out) {
        out.writeInt(msg.flags.size());
        msg.flags.stream().map(IUserData.Flags::toString).forEach(out::writeUtf);
    }

    public static FlagsUpdatePacket decode(PacketBuffer in) {
        int size = in.readInt();
        Set<IUserData.Flags> flags = new HashSet<>();
        for(int i = 0; i<size; i++) {
            String flag = in.readUtf(64);
            flags.add(IUserData.Flags.valueOf(flag));
        }
        return new FlagsUpdatePacket(flags);
    }

    public static boolean handle(FlagsUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isClient()) {
            return true;
        }

        ArmaCraft.getInstance().getClientDist().ifPresent(client -> client.getUserData().setFlags(msg.flags));
        return true;
    }
}
