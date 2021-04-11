package org.armacraft.mod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.craftingdead.core.capability.living.PlayerImpl;

@Mixin(PlayerImpl.class)
public class PlayerImplMixin {

	/**
	 * Impede que o player seja infectado
	 */
	@Inject(method = "infect", remap = false, at = @At("HEAD"), cancellable = true)
	public void infect(float chance, CallbackInfo ci) {
		ci.cancel();
	}

	/**
	 * Impede que a perna seja quebrada
	 */
	@Inject(method = "updateBrokenLeg", remap = false, at = @At("HEAD"), cancellable = true)
	public void updateBrokenLeg(CallbackInfo ci) {
		ci.cancel();
	}
}
