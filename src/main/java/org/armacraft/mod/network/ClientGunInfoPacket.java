package org.armacraft.mod.network;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.armacraft.mod.util.GunUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientGunInfoPacket {
    private String gunResourceLocation;
    private int rpm;
    private int reloadDurationTicks;
    private float accuracyPct;
    private int bulletAmountToFire;

    public ClientGunInfoPacket(String gunResourceLocation, float accuracyPct, int rpm, int bulletAmountToFire, int reloadDurationTicks) {
        this.gunResourceLocation = gunResourceLocation;
        this.accuracyPct = accuracyPct;
        this.bulletAmountToFire = bulletAmountToFire;
        this.rpm = rpm;
        this.reloadDurationTicks = reloadDurationTicks;
    }

    public static void encode(ClientGunInfoPacket msg, PacketBuffer out) {
        // @StringObfuscator:on
    	out.writeUtf(msg.gunResourceLocation);
        out.writeInt(msg.rpm);
        out.writeInt(msg.reloadDurationTicks);
        out.writeFloat(msg.accuracyPct);
        out.writeInt(msg.bulletAmountToFire);
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

        return new ClientGunInfoPacket(gunId, accuracy, rpm, bulletAmountToFire, reloadDurationTicks);
    }

    public static boolean handle(ClientGunInfoPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isServer()) {
            return true;
        }

        if(!GunUtils.INTEGRITY_VALIDATOR.test(msg)) {
        	ctx.get().getSender().setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        }

        return true;
    }

    public String getGunResourceLocation() {
        return gunResourceLocation;
    }

    public int getRpm() {
        return rpm;
    }

    public int getReloadDurationTicks() {
        return reloadDurationTicks;
    }

    public float getAccuracyPct() {
        return accuracyPct;
    }

    public int getBulletAmountToFire() {
        return bulletAmountToFire;
    }
}
