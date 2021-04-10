package org.armacraft.mod.mixin;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.bridge.IGunImplBridge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.craftingdead.core.capability.gun.GunClientImpl;
import com.craftingdead.core.capability.gun.GunImpl;
import com.craftingdead.core.capability.living.ILiving;
import com.craftingdead.core.client.ClientDist;
import com.craftingdead.core.item.AttachmentItem.MultiplierType;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;

@Mixin(GunClientImpl.class)
public abstract class GunClientImplMixin {

	@Shadow()
	@Final()
	private ClientDist client;

	@Shadow()
	@Final()
	private Minecraft minecraft;

	@Shadow()
	@Final()
	private GunImpl gun;

	/**
	 * Faz com que seja aplicado o jolt contendo o spread original da arma caso
	 * estiver mirando, já que pro Crafting Dead, não existe mais recoil, e sim
	 * accuracy. A accuracy enquanto mira é sempre mínima.
	 */
	@Inject(method = "handleShoot", remap = false, at = @At("HEAD"))
	public void handleShoot(ILiving<?, ?> living, CallbackInfo ci) {
		LivingEntity theEntity = living.getEntity();
		if (ArmaCraft.isAiming(theEntity)) {

			if (theEntity == this.minecraft.getCameraEntity()) {
				this.client.getCameraManager()
						.joltCamera(1.0F - ((IGunImplBridge) this.gun).bridge$getGunProvider().getAccuracyPct()
								* gun.getAttachmentMultiplier(MultiplierType.ACCURACY), true);
			}
		}
	}
}
