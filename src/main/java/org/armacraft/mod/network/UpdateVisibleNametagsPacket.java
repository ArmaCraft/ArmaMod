package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.ArmaCraft;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class UpdateVisibleNametagsPacket {
    private Set<String> visibleNameTags;

    private UpdateVisibleNametagsPacket(Set<String> visibleNameTags) {
        this.visibleNameTags = visibleNameTags;
    }

    public static void encode(UpdateVisibleNametagsPacket msg, PacketBuffer out) {
        out.writeByte(msg.visibleNameTags.size());
        msg.visibleNameTags.forEach(nametag -> out.writeByteArray(nametag.getBytes()));
    }

    public static UpdateVisibleNametagsPacket decode(PacketBuffer in) {
        UpdateVisibleNametagsPacket packet = UpdateVisibleNametagsPacket.empty();

        byte amount = in.readByte();

        for (int i = 0; i < amount; i++) {
            try {
                packet.visibleNameTags.add(new String(in.readByteArray(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return packet;
    }

    public static boolean handle(UpdateVisibleNametagsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> ArmaCraft.VISIBLE_NAMETAGS = msg.visibleNameTags);
        }
        return true;
    }

    public static UpdateVisibleNametagsPacket empty() {
        return new UpdateVisibleNametagsPacket(new HashSet<>());
    }
}
