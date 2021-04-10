package org.armacraft.mod;

import org.armacraft.mod.init.ClientDist;
import org.armacraft.mod.init.ModBlocks;
import org.armacraft.mod.init.ModItems;
import org.armacraft.mod.init.ModTileEntityTypes;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.capability.gun.AimableGun;
import com.craftingdead.core.capability.gun.GunImpl;
import com.craftingdead.core.event.GunEvent;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ArmaCraft.MODID)
public class ArmaCraft {

	public static final String MODID = "armacraft";
	public static PermissionChecker PERMISSION_CHECKER;
	public static IEventBus modEventBus;

	public static float ARMACRAFT_HEADSHOT_MULTIPLIER = 1.5F;

	public ArmaCraft() {
		modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.register(this);

		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientDist::new);
	}
	
	/**
	 * Listener pro {@link MinecraftForge}.EVENT_BUS
	 */
	@Mod.EventBusSubscriber
	public static class EventBusListener {

		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public void onGunHit(GunEvent.HitEntity event) {
			if (event.isHeadshot()) {
				// Matematicamente remove o multiplier de headshot do dano e aplica o nosso no
				// lugar
				event.setDamage((event.getDamage() / GunImpl.HEADSHOT_MULTIPLIER) * ARMACRAFT_HEADSHOT_MULTIPLIER);
			}
		}

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
