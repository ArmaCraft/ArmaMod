package org.armacraft.mod.network;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.armacraft.mod.util.GunUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.wrapper.GunInfoWrapper;

public class ClientGunInfoPacket {
    private GunInfoWrapper gunInfos;

    public ClientGunInfoPacket(GunInfoWrapper infos) {
        this.gunInfos = infos;
    }

    public static void encode(ClientGunInfoPacket msg, PacketBuffer out) {
        // @StringObfuscator:on
    	out.writeUtf(msg.gunInfos.getGunResourcePath());
        out.writeInt(msg.gunInfos.getRpm());
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

        return new ClientGunInfoPacket(new GunInfoWrapper(gunId, rpm, reloadDurationTicks, accuracy, bulletAmountToFire));
    }

    public static boolean handle(ClientGunInfoPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isServer()) {
            return true;
        }

        if(!GunUtils.INTEGRITY_VALIDATOR.test(msg.gunInfos)) {
        	ctx.get().getSender().setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }

        return true;
    }
}
