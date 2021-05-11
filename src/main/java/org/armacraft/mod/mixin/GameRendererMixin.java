package org.armacraft.mod.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.armacraft.mod.util.GunUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	
	@Shadow private Minecraft minecraft;

	/**
	 * Remove view bobbing se estiver mirando
	 */
	@Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
	private void bobView(MatrixStack stack, float p_228383_2_, CallbackInfo ci) {
		PlayerEntity myPlayer = this.minecraft.player;
		
		if (myPlayer != null && GunUtils.isAiming(myPlayer)) {
			ci.cancel();
		}
	}
}
