package org.armacraft.mod.mixin;

import com.craftingdead.core.living.PlayerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerImpl.class)
public class PlayerImplMixin {

	/**
	 * Impede que a perna seja quebrada
	 */
	@Inject(method = "updateBrokenLeg", remap = false, at = @At("HEAD"), cancellable = true)
	public void updateBrokenLeg(CallbackInfo ci) {
		ci.cancel();
	}
}
