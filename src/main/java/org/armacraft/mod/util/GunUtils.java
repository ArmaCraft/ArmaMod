package org.armacraft.mod.util;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.item.gun.aimable.AimableGun;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.armacraft.mod.server.CustomGunDataController;
import org.armacraft.mod.wrapper.ClientGunDataWrapper;
import org.armacraft.mod.wrapper.CommonGunDataWrapper;

import java.util.function.Predicate;

public class GunUtils {

    public static Predicate<ClientGunDataWrapper> INTEGRITY_VALIDATOR = (clientInfo) -> {
        CustomGunDataController controller = CustomGunDataController.INSTANCE;
        if(controller.getCachedGunData().get(clientInfo.getResourceLocation().toString()) != null) {
            CommonGunDataWrapper serverGunInfo = controller.getCachedGunData().get(clientInfo.getResourceLocation().toString());
            return serverGunInfo.getAccuracyPct() == clientInfo.getAccuracyPct()
                    && serverGunInfo.getFireRateRPM() == clientInfo.getFireRateRPM()
                    && serverGunInfo.getBulletAmountToFire() == clientInfo.getBulletAmountToFire()
                    && serverGunInfo.getReloadDurationTicks() == clientInfo.getReloadDurationTicks();
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
