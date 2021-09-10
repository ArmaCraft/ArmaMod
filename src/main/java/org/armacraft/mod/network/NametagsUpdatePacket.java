package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterfaceImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class NametagsUpdatePacket {
    private Set<String> nametags;

    public NametagsUpdatePacket(Set<String> nametags) {
        this.nametags = nametags;
    }

    public static void encode(NametagsUpdatePacket msg, PacketBuffer out) {
        out.writeInt(msg.nametags.size());
        for(String nametag : msg.nametags) {
            out.writeUtf(nametag);
        }
    }

    public static NametagsUpdatePacket decode(PacketBuffer in) {
        int size = in.readInt();
        Set<String> nametags = new HashSet<>();
        for(int i = 0; i<size; i++) {
            nametags.add(in.readUtf(64));
        }
        return new NametagsUpdatePacket(nametags);
    }

    public static boolean handle(NametagsUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> ArmaCraft.getInstance().getClientDist().get().getUserData().setNametagWhitelist(msg.nametags));
        }
        return true;
    }
}
