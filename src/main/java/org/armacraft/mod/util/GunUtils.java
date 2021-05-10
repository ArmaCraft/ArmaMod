package org.armacraft.mod.util;

import java.util.Optional;
import java.util.function.Predicate;

import org.armacraft.mod.wrapper.ClientGunInfoWrapper;
import org.armacraft.mod.wrapper.CommonGunInfoWrapper;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.item.ModItems;
import com.craftingdead.core.item.gun.aimable.AimableGun;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;

public class GunUtils {

    public static Predicate<ClientGunInfoWrapper> INTEGRITY_VALIDATOR = (clientInfo) -> {
        if(getCommonGunSpecsWrapper(clientInfo.getResourceLocation()).isPresent()) {
            CommonGunInfoWrapper serverGunInfo = getCommonGunSpecsWrapper(clientInfo.getResourceLocation()).get();
            return serverGunInfo.getAccuracyPct() == clientInfo.getAccuracyPct()
                    && serverGunInfo.getFireRateRPM() == clientInfo.getFireRateRPM()
                    && serverGunInfo.getBulletAmountToFire() == clientInfo.getBulletAmountToFire()
                    && serverGunInfo.getReloadDurationTicks() == clientInfo.getReloadDurationTicks();
        }
        return false;
    };

    public static Optional<CommonGunInfoWrapper> getCommonGunSpecsWrapper(String resourceLocation) {
        Optional<GunItem> gunItemOpt = RegistryUtil.filterRegistries(GunItem.class, ModItems.ITEMS)
                .stream().filter(registry -> registry.getId().toString().equalsIgnoreCase(resourceLocation))
                .map(gun -> (GunItem) gun.get()).findFirst();
        if(gunItemOpt.isPresent()) {
            GunItem gunItem = gunItemOpt.get();
            return Optional.of(new CommonGunInfoWrapper(
                    gunItem.getRegistryName().toString(),
                    gunItem.getGunType().getFireRateRPM(),
                    gunItem.getGunType().getFireDelayMs(),
                    gunItem.getGunType().getDamage(),
                    gunItem.getGunType().getReloadDurationTicks(),
                    gunItem.getGunType().getAccuracyPct(),
                    gunItem.getGunType().getBulletAmountToFire()));
        }
        return Optional.empty();
    }

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
