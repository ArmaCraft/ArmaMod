package org.armacraft.mod.util;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.capability.gun.AimableGun;

import com.craftingdead.core.capability.gun.GunImpl;
import com.craftingdead.core.capability.gun.IGun;
import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.client.ClientDist;
import org.armacraft.mod.network.ClientGunInfoPacket;
import org.armacraft.mod.wrapper.GunInfoWrapper;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class GunUtils {
    public static Function<String, Optional<GunInfoWrapper>> GET_SERVER_GUN_INFO = (path) -> {
        Optional<RegistryObject<Item>> optItem = MiscUtil.GET_CD_REGISTRY.apply(path);
        if (optItem.isPresent()) {
            Item item = optItem.get().get();
            if (item instanceof GunItem) {
                GunItem gunItem = (GunItem) item;
                return Optional.of(
                        new GunInfoWrapper(path,
                                gunItem.getFireRateRPM(),
                                gunItem.getReloadDurationTicks(),
                                gunItem.getAccuracyPct(),
                                gunItem.getBulletAmountToFire()));
            }
        }
        return Optional.empty();
    };

    public static Predicate<GunInfoWrapper> INTEGRITY_VALIDATOR = (info) -> {
        if(GET_SERVER_GUN_INFO.apply(info.getGunResourcePath()).isPresent()) {
            GunInfoWrapper serverGunInfo = GET_SERVER_GUN_INFO.apply(info.getGunResourcePath()).get();
            return serverGunInfo.getAccuracyPct() == info.getAccuracyPct()
                    && serverGunInfo.getRpm() == info.getRpm()
                    && serverGunInfo.getBulletAmountToFire() == info.getBulletAmountToFire()
                    && serverGunInfo.getReloadDurationTicks() == info.getReloadDurationTicks();
        }
        return false;
    };

    public static boolean isAiming(LivingEntity livingEntity) {
        // Getters de ItemStacks não retornam mais null em mods até onde sei
        ItemStack heldItem = livingEntity.getMainHandItem();

        return heldItem.getCapability(ModCapabilities.GUN).map(gun -> {
            if (gun instanceof AimableGun) {
                AimableGun aimableGun = (AimableGun) gun;
                if (aimableGun.isAiming(livingEntity)) {
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }
}
