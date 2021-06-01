package org.armacraft.mod.mixin;

import com.craftingdead.core.item.gun.AbstractGun;
import com.craftingdead.core.item.gun.AbstractGunType;
import com.craftingdead.core.item.gun.IGunClient;
import com.craftingdead.core.item.gun.PendingHit;
import com.craftingdead.core.item.gun.ammoprovider.IAmmoProvider;
import com.craftingdead.core.living.ILiving;
import com.craftingdead.core.living.IPlayer;
import com.craftingdead.core.util.RayTraceUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import org.armacraft.mod.bridge.AbstractGunBridge;
import org.armacraft.mod.bridge.bukkit.IBukkitWorldGuardBridge;
import org.armacraft.mod.potion.ArmaCraftEffects;
import org.armacraft.mod.server.ServerDist;
import org.armacraft.mod.server.bukkit.util.ForgeToBukkitInterfaceImpl;
import org.armacraft.mod.util.GunUtils;
import org.armacraft.mod.util.MiscUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Mixin(AbstractGun.class)
public abstract class AbstractGunMixin<T extends AbstractGunType<SELF>, SELF extends AbstractGun<T, SELF>> implements AbstractGunBridge<T, SELF> {

	@Shadow @Final private IGunClient client;

	@Shadow protected abstract void hitBlock(ILiving<?, ?> living, BlockRayTraceResult rayTrace, boolean playSound);

	@Shadow public abstract float getAccuracy(ILiving<?, ?> living);

	@Shadow private int shotCount;
	@Shadow @Final private static Random random;
	@Shadow private IAmmoProvider ammoProvider;
	@Shadow @Final protected T type;
	
	@Shadow
	@Final
	protected ItemStack gunStack;
	
	@Accessor(value = "type", remap = false) public abstract T bridge$getGunType();

	/**
	 * Mecânica de retornar bala
	 * @author
	 */
	@Overwrite(remap = false)
	protected void processShot(ILiving<?, ?> living, ThreadTaskExecutor<?> executor) {
		final Entity entity = living.getEntity();

		boolean consumeBullet = true;

		// Used to avoid playing the same hit sound more than once.
		RayTraceResult lastRayTraceResult = null;
		for (int i = 0; i < this.type.getBulletAmountToFire(); i++) {
			final long randomSeed = entity.level.getGameTime() + i;
			random.setSeed(randomSeed);

			RayTraceResult rayTraceResult =
					CompletableFuture.supplyAsync(() -> RayTraceUtil.rayTrace(entity,
							this.type.getRange(),
							this.getAccuracy(living),
							this.shotCount,
							random).orElse(null), executor).join();

			if (rayTraceResult != null) {
				switch (rayTraceResult.getType()) {
					case BLOCK:
						final BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTraceResult;
						final boolean playSound;
						if (lastRayTraceResult instanceof BlockRayTraceResult) {
							playSound = entity.level.getBlockState(
									((BlockRayTraceResult) lastRayTraceResult).getBlockPos()) != entity.level
									.getBlockState(blockRayTraceResult.getBlockPos());
						} else {
							playSound = true;
						}

						executor.execute(() -> this.hitBlock(living, (BlockRayTraceResult) rayTraceResult,
								playSound && entity.level.isClientSide()));
						break;
					case ENTITY:
						EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
						if (!entityRayTraceResult.getEntity().isAlive()) {
							break;
						}

						// Handled by validatePendingHit
						if (entityRayTraceResult.getEntity() instanceof ServerPlayerEntity
								&& entity instanceof ServerPlayerEntity) {
							break;
						}

						if (entity.level.isClientSide()) {
							this.client.handleHitEntityPre(living,
									entityRayTraceResult.getEntity(),
									entityRayTraceResult.getLocation(),
									randomSeed);
						}

						final boolean playEntityHitSound = !(lastRayTraceResult instanceof EntityRayTraceResult)
								|| !((EntityRayTraceResult) lastRayTraceResult).getEntity().getType()
								.getRegistryName()
								.equals(entityRayTraceResult.getEntity().getType().getRegistryName())
								&& entity.level.isClientSide();

						executor.execute(() -> this.hitEntity(living, entityRayTraceResult.getEntity(),
								entityRayTraceResult.getLocation(), playEntityHitSound));

						if (!entity.level.isClientSide()
								&& !(living.getEntity() instanceof PlayerEntity
								&& ((PlayerEntity) living.getEntity()).isCreative())) {
							if (ServerDist.WORLD_GUARD_BRIDGE == null) {
								break;
							}
							if(ForgeToBukkitInterfaceImpl.INSTANCE.isWorldGuardFlagAllowed("bullet-recovery", entity)) {
								consumeBullet = false;
							}
							break;
						}

						break;
					default:
						break;
				}

				if(ServerDist.WORLD_GUARD_BRIDGE != null
						&& ForgeToBukkitInterfaceImpl.INSTANCE.isWorldGuardFlagAllowed("infinity-ammo", entity)) {
					consumeBullet = false;
				}

				if (!entity.level.isClientSide()
						&& !(living.getEntity() instanceof PlayerEntity
						&& ((PlayerEntity) living.getEntity()).isCreative())) {
					if (consumeBullet) {
						this.ammoProvider.getExpectedMagazine().decrementSize();
					}
				}

				lastRayTraceResult = rayTraceResult;
			}
		}


	}

	/**
	 * Faz com que os tiros vão 100% retos se estiver mirando
	 */
	@Inject(method = "getAccuracy", remap = false, at = @At("HEAD"), cancellable = true)
	public void getAccuracy(ILiving<?, ?> living, CallbackInfoReturnable<Float> ci) {
		if (GunUtils.isAiming(living.getEntity())) {
			ci.setReturnValue(1F);
		}
	}

	/**
	 * Faz com que os players não consigam atirar enquanto estiverem planando
	 * (exemplo, de elytra ou via plugin)
	 */
	@Inject(method = "canShoot", remap = false, at = @At("HEAD"), cancellable = true)
	public void canShoot(ILiving<?, ?> living, CallbackInfoReturnable<Boolean> ci) {
		if (living.getEntity().isFallFlying()) {
			ci.setReturnValue(false);
		}
	}

	/**
	 * SEMPRE confia nos tiros dos players, evitando o sistema de teste de colisão por rollbacks do CDA
	 */
	@Overwrite(remap = false)
	public void validatePendingHit(IPlayer<ServerPlayerEntity> player, ILiving<?, ?> hitLiving, PendingHit pendingHit) {
		if (!hitLiving.getEntity().isDeadOrDying()) {
			// Acertar o tiro
			this.hitEntity(player, hitLiving.getEntity(), pendingHit.getHitSnapshot().getPos(), false);
			String gunPath = this.gunStack.getItem().getRegistryName().getPath();
			if(gunPath.equalsIgnoreCase("awp") || gunPath.equals("m107")) {
				Effect speedEffect = ArmaCraftEffects.ARMACRAFT_SPEED.get();
				if(hitLiving.getEntity().getActiveEffectsMap().containsKey(speedEffect)) {
					MiscUtil.playSoundAtEntity(hitLiving.getEntity(), SoundEvents.STONE_BREAK, 1.0f, 1.0f);
					hitLiving.getEntity().sendMessage(new TranslationTextComponent("message.sniper_stun")
							.setStyle(Style.EMPTY.applyFormat(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
					hitLiving.getEntity().removeEffect(speedEffect);
				}
			}
		}
		
		// Som de plim
		MiscUtil.playSoundToPlayer(player.getEntity(), SoundEvents.ARROW_HIT_PLAYER, 2F, 1.1F);
	}
	
	@Shadow() private void hitEntity(ILiving<?, ?> living, Entity hitEntity, Vector3d hitPos, boolean playSound) {}
}
