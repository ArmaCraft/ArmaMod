package org.armacraft.mod.network;

import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.item.ModItems;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.bridge.IAbstractGunTypeBridge;
import org.armacraft.mod.util.RegistryUtil;
import org.armacraft.mod.wrapper.CommonGunInfoWrapper;
import org.armacraft.mod.wrapper.ResourceLocationWrapper;

import java.util.function.Supplier;

public class CommonGunSpecsUpdatePacket {
    private CommonGunInfoWrapper infos;

    public CommonGunSpecsUpdatePacket(CommonGunInfoWrapper wrapper) {
        this.infos = wrapper;
    }

    public static void encode(CommonGunSpecsUpdatePacket msg, PacketBuffer out) {
        out.writeUtf(msg.infos.getResourceLocation().toString());
        out.writeInt(msg.infos.getFireRateRPM());
        out.writeFloat(msg.infos.getAccuracyPct());
        out.writeInt(msg.infos.getFireDelayMs());
        out.writeInt(msg.infos.getReloadDurationTicks());
        out.writeFloat(msg.infos.getDamage());
        out.writeInt(msg.infos.getBulletAmountToFire());
    }

    public static CommonGunSpecsUpdatePacket decode(PacketBuffer in) {
        String id = in.readUtf(64);
        int rpm = in.readInt();
        float accuracy = in.readFloat();
        int fireDelay = in.readInt();
        int reloadTicks = in.readInt();
        float damage = in.readFloat();
        int bulletAmount = in.readInt();

        return new CommonGunSpecsUpdatePacket(
                new CommonGunInfoWrapper(ResourceLocationWrapper.of(id), rpm, fireDelay, damage, reloadTicks, accuracy, bulletAmount));
    }

    public static boolean handle(CommonGunSpecsUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        RegistryUtil.filterRegistries(GunItem.class, ModItems.ITEMS).stream()
                .filter(registry -> registry.getId().toString().equals(msg.infos.getResourceLocation().toString()))
                .forEach(gun -> ((IAbstractGunTypeBridge) ((GunItem) gun.get()).getGunType()).bridge$updateSpecs(msg.infos));
        return true;
    }
}
