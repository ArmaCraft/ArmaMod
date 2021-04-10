package org.armacraft.mod.util;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.capability.gun.AimableGun;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class GunUtils {
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
