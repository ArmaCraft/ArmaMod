package org.armacraft.mod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.craftingdead.core.client.gui.HitMarker;
import com.craftingdead.core.client.gui.IngameGui;

@Mixin(IngameGui.class)
public class IngameGuiMixin {

	/**
	 * Não mostra hitmarkers
	 */
	@Inject(method = "displayHitMarker", remap = false, cancellable = true, at = @At("HEAD"))
	public void displayHitMarker(HitMarker hitMarker, CallbackInfo ci) {
		ci.cancel();
	}
}
