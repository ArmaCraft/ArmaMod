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
import org.armacraft.mod.network.ClientGunInfoPacket;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class GunUtils {
	public static Predicate<ClientGunInfoPacket> INTEGRITY_VALIDATOR = (info) -> {
		if(ArmaCraft.getInstance().getServerDist().isPresent()) {
			Optional<RegistryObject<Item>> optItem = MiscUtil.GET_CD_REGISTRY.apply(info.getGunResourceLocation());
			if(optItem.isPresent()) {
				Item item = optItem.get().get();
				if(item instanceof GunItem) {
					GunItem serverGun = (GunItem) item;
					return serverGun.getAccuracyPct() == info.getAccuracyPct()
							&& serverGun.getFireRateRPM() == info.getRpm()
							&& serverGun.getBulletAmountToFire() == info.getBulletAmountToFire()
							&& serverGun.getReloadDurationTicks() == info.getReloadDurationTicks();
				}
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
