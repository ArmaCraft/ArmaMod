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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class GunUtils {
	public static Predicate<GunItem> INTEGRITY_VALIDATOR = (gun) -> {
		if(ArmaCraft.getInstance().getServerDist().isPresent()) {
			if(MiscUtil.GET_CD_REGISTRY.apply(gun.getRegistryName().getPath()).isPresent()) {
				GunItem serverGun = (GunItem) MiscUtil.GET_CD_REGISTRY.apply(gun.getRegistryName().getPath()).get().get();
				return serverGun.getAccuracyPct() == gun.getAccuracyPct()
						&& serverGun.getDamage() == gun.getDamage()
						&& serverGun.getFireRateRPM() == gun.getFireRateRPM()
						&& serverGun.getBulletAmountToFire() == gun.getBulletAmountToFire();
			}
			return false;
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
