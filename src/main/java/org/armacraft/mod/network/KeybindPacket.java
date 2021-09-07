package org.armacraft.mod.network;

import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.item.ModItems;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.bridge.IAbstractGunTypeBridge;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterfaceImpl;
import org.armacraft.mod.util.RegistryUtil;
import org.armacraft.mod.wrapper.CommonGunDataWrapper;
import org.armacraft.mod.wrapper.ResourceLocationWrapper;

import java.util.function.Supplier;

public class KeybindPacket {
    private Character key;

    public KeybindPacket(Character key) {
        this.key = key;
    }

    public static void encode(KeybindPacket msg, PacketBuffer out) {
        out.writeChar(msg.key);
    }

    public static KeybindPacket decode(PacketBuffer in) {
        return new KeybindPacket(in.readChar());
    }

    public static boolean handle(KeybindPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isServer()) {
            return true;
        }

        ForgeToBukkitInterfaceImpl.INSTANCE.onKeybind(ctx.get().getSender(), msg.key);
        return true;
    }
}
