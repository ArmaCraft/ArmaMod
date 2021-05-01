package org.armacraft.mod.mixin;

import org.armacraft.mod.bridge.AbstractGunBridge;
import org.armacraft.mod.potion.ArmaCraftEffects;
import org.armacraft.mod.server.ServerDist;
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

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.item.PaintItem;
import com.craftingdead.core.item.gun.AbstractGun;
import com.craftingdead.core.item.gun.AbstractGunType;
import com.craftingdead.core.item.gun.PendingHit;
import com.craftingdead.core.living.ILiving;
import com.craftingdead.core.living.IPlayer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

@Mixin(AbstractGun.class)
public abstract class AbstractGunMixin<T extends AbstractGunType<SELF>, SELF extends AbstractGun<T, SELF>> implements AbstractGunBridge<T, SELF> {

	@Shadow @Final protected T type;
	
	@Shadow
	@Final
	protected ItemStack gunStack;
	
	@Accessor(value = "type", remap = false) public abstract T bridge$getGunType();

	/**
	 * Remove skins ao atirar, se não tiver perm
	 */

	@Inject(method = "processShot", remap = false, at = @At("TAIL"))
	private void processShot(ILiving<?, ?> living, ThreadTaskExecutor<?> threadTaskExecutor, CallbackInfo ci) {
		threadTaskExecutor.execute(() -> {
			if (living.getEntity() instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity) living.getEntity();
				gunStack.getCapability(ModCapabilities.GUN).ifPresent(gunController -> {
					if (gunController.getPaint().isPresent()) {
						PaintItem paint = (PaintItem) gunController.getPaintStack().getItem();
						String permissionNode = "armacraft.skins."
								+ gunStack.getItem().getRegistryName().getPath() + "."
								+ paint.getRegistryName().getPath();
						if (!ServerDist.PERMISSION_BRIDGE.hasPermission(playerEntity.getUUID(), permissionNode)) {
							playerEntity.sendMessage(new TranslationTextComponent("message.no_skin_permission")
									.setStyle(Style.EMPTY.applyFormat(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
							gunStack.getCapability(ModCapabilities.GUN).ifPresent(x -> x.setPaintStack(ItemStack.EMPTY));
						}
					}
				});
			}
		});
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
