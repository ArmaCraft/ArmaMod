package org.armacraft.mod.network;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterfaceImpl;
import org.armacraft.mod.util.GunUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.wrapper.ClientGunInfoWrapper;

public class ClientGunInfoPacket {
    private ClientGunInfoWrapper gunInfos;

    public ClientGunInfoPacket(ClientGunInfoWrapper infos) {
        this.gunInfos = infos;
    }

    public static void encode(ClientGunInfoPacket msg, PacketBuffer out) {
        // @StringObfuscator:on
    	out.writeUtf(msg.gunInfos.getResourceLocation().toString());
        out.writeInt(msg.gunInfos.getFireRateRPM());
        out.writeInt(msg.gunInfos.getReloadDurationTicks());
        out.writeFloat(msg.gunInfos.getAccuracyPct());
        out.writeInt(msg.gunInfos.getBulletAmountToFire());
        // @StringObfuscator:off
    }

    public static ClientGunInfoPacket decode(PacketBuffer in) {
        // @StringObfuscator:on
        String gunId = in.readUtf(60);
        int rpm = in.readInt();
        int reloadDurationTicks = in.readInt();
        float accuracy = in.readFloat();
        int bulletAmountToFire = in.readInt();
        // @StringObfuscator:off

        return new ClientGunInfoPacket(new ClientGunInfoWrapper(gunId, rpm, reloadDurationTicks, accuracy, bulletAmountToFire));
    }

    public static boolean handle(ClientGunInfoPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isServer()) {
            return true;
        }

        if(!GunUtils.INTEGRITY_VALIDATOR.test(msg.gunInfos)) {
        	ctx.get().getSender().setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            ForgeToBukkitInterfaceImpl.INSTANCE.onGunNoIntegrity(ctx.get().getSender(), msg.gunInfos, GunUtils.getCommonGunSpecsWrapper(msg.gunInfos.getResourceLocation().toString()));
        }

        return true;
    }
}
