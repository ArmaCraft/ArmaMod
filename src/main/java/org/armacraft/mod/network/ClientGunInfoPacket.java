package org.armacraft.mod.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.server.CustomGunDataController;
import org.armacraft.mod.server.ServerDist;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterfaceImpl;
import org.armacraft.mod.util.GunUtils;
import org.armacraft.mod.wrapper.ClientGunDataWrapper;
import org.armacraft.mod.wrapper.ResourceLocationWrapper;

import java.util.function.Supplier;

public class ClientGunInfoPacket {
    private ClientGunDataWrapper gunInfos;

    public ClientGunInfoPacket(ClientGunDataWrapper infos) {
        this.gunInfos = infos;
    }

    public static void encode(ClientGunInfoPacket msg, PacketBuffer out) {
        // @StringObfuscator:on
    	out.writeUtf(msg.gunInfos.getResourceLocation().toString());
        out.writeInt(msg.gunInfos.getFireDelayMs());
        out.writeInt(msg.gunInfos.getReloadDurationTicks());
        out.writeFloat(msg.gunInfos.getAccuracyPct());
        out.writeInt(msg.gunInfos.getBulletAmountToFire());
        // @StringObfuscator:off
    }

    public static ClientGunInfoPacket decode(PacketBuffer in) {
        // @StringObfuscator:on
        String gunId = in.readUtf(60);
        int delayMs = in.readInt();
        int reloadDurationTicks = in.readInt();
        float accuracy = in.readFloat();
        int bulletAmountToFire = in.readInt();
        // @StringObfuscator:off

        return new ClientGunInfoPacket(new ClientGunDataWrapper(ResourceLocationWrapper.of(gunId), delayMs, reloadDurationTicks, accuracy, bulletAmountToFire));
    }

    public static boolean handle(ClientGunInfoPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isServer()) {
            return true;
        }

        if(!GunUtils.INTEGRITY_VALIDATOR.test(msg.gunInfos)) {
        	if(System.currentTimeMillis() - ServerDist.get().getGunUpdateBeginMillis() < ServerDist.GUN_UPDATE_TOLERANCE_MILLIS) {
                return false;
        	}
        	ctx.get().getSender().setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
        	CustomGunDataController.INSTANCE.getCommonGunData(msg.gunInfos.getResourceLocation()).ifPresent(data -> {
        	    ForgeToBukkitInterfaceImpl.INSTANCE.onGunNoIntegrity(ctx.get().getSender(), msg.gunInfos, CustomGunDataController.INSTANCE.getCommonGunData(msg.gunInfos.getResourceLocation()));
        	});
        }

        return true;
    }
}
