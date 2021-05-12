package org.armacraft.mod.network;

import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.item.ModItems;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.bridge.IAbstractGunTypeBridge;
import org.armacraft.mod.util.RegistryUtil;
import org.armacraft.mod.wrapper.CommonGunDataWrapper;
import org.armacraft.mod.wrapper.ResourceLocationWrapper;

import java.util.function.Supplier;

public class CommonGunSpecsUpdatePacket {
    private CommonGunDataWrapper infos;

    public CommonGunSpecsUpdatePacket(CommonGunDataWrapper wrapper) {
        this.infos = wrapper;
    }

    public static void encode(CommonGunSpecsUpdatePacket msg, PacketBuffer out) {
        out.writeUtf(msg.infos.getResourceLocation().toString());
        out.writeFloat(msg.infos.getAccuracyPct());
        out.writeInt(msg.infos.getFireDelayMs());
        out.writeInt(msg.infos.getReloadDurationTicks());
        out.writeFloat(msg.infos.getDamage());
        out.writeInt(msg.infos.getBulletAmountToFire());
        out.writeFloat(msg.infos.getHeadshotMultiplier());
    }

    public static CommonGunSpecsUpdatePacket decode(PacketBuffer in) {
        String id = in.readUtf(64);
        float accuracy = in.readFloat();
        int fireDelay = in.readInt();
        int reloadTicks = in.readInt();
        float damage = in.readFloat();
        int bulletAmount = in.readInt();
        float headshotMultiplier = in.readFloat();

        return new CommonGunSpecsUpdatePacket(
                new CommonGunDataWrapper(ResourceLocationWrapper.of(id), fireDelay, damage, reloadTicks, accuracy, bulletAmount, headshotMultiplier));
    }

    public static boolean handle(CommonGunSpecsUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        RegistryUtil.filterRegistries(GunItem.class, ModItems.ITEMS).stream()
                .filter(registry -> registry.getId().toString().equalsIgnoreCase(msg.infos.getResourceLocation().toString()))
                .forEach(gun -> {
                    ((IAbstractGunTypeBridge) ((GunItem) gun.get()).getGunType()).bridge$updateSpecs(msg.infos);
                });
        return true;
    }
}
