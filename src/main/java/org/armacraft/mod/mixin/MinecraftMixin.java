package org.armacraft.mod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	/**
	 * Seta o titulo do jogo pro nosso proprio
	 */
	@Inject(method = "createTitle", at = @At("RETURN"), cancellable = true)
	public void createTitle(CallbackInfoReturnable<String> a) {
		a.setReturnValue("ARMACRAFT - "+a.getReturnValue());
	}
}