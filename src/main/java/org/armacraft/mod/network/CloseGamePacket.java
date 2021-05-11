package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.client.util.ClientUtils;

import java.util.function.Supplier;

public class CloseGamePacket {
    private String title;
    private String reason;

    public CloseGamePacket(String title, String reason) {
        this.title = title;
        this.reason = reason;
    }

    public static void encode(CloseGamePacket msg, PacketBuffer out) {
        out.writeByteArray(msg.title.getBytes());
        out.writeByteArray(msg.reason.getBytes());
    }

    public static CloseGamePacket decode(PacketBuffer in) {
        return new CloseGamePacket(in.readUtf(), in.readUtf());
    }

    public static boolean handle(CloseGamePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                if((msg.title != null && msg.title.isEmpty()) || (msg.reason != null && msg.reason.isEmpty())) {
                    ClientUtils.openFrameWith(msg.title, msg.reason);
                }
                ClientUtils.silentlyMakeGameStop();
            });
        }
        return true;
    }
}
