package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterfaceImpl;
import org.armacraft.mod.wrapper.KeyBindWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class KeybindingsUpdatePacket {
    private List<KeyBindWrapper> binds;

    public KeybindingsUpdatePacket(List<KeyBindWrapper> binds) {
        this.binds = binds;
    }

    public static void encode(KeybindingsUpdatePacket msg, PacketBuffer out) {
        out.writeInt(msg.binds.size());
        msg.binds.stream().map(x -> x.getBind() + ":" + x.getCommand()).forEach(out::writeUtf);
    }

    public static KeybindingsUpdatePacket decode(PacketBuffer in) {
        int size = in.readInt();
        List<KeyBindWrapper> binds = new ArrayList<>();
        for(int i = 0; i<size; i++) {
            String bind = in.readUtf(64);
            binds.add(new KeyBindWrapper(bind.charAt(0), bind.split(":")[1]));
        }
        return new KeybindingsUpdatePacket(binds);
    }

    public static boolean handle(KeybindingsUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isClient()) {
            return true;
        }

        ArmaCraft.getInstance().getClientDist().ifPresent(client -> client.getUserData().setKeyBinds(new HashSet<>(msg.binds)));
        return true;
    }
}
