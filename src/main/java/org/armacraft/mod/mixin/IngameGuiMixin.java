package org.armacraft.mod.mixin;

import com.craftingdead.core.client.ClientDist;
import com.craftingdead.core.client.gui.HitMarker;
import com.craftingdead.core.client.gui.IngameGui;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameGui.class)
public class IngameGuiMixin {
	@Shadow
	@Mutable
	@Final
	private static int KILL_FEED_MESSAGE_LIFE_MS;

	/**
	 * Não mostra hitmarkers
	 */
	@Inject(method = "displayHitMarker", remap = false, cancellable = true, at = @At("HEAD"))
	public void displayHitMarker(HitMarker hitMarker, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "<init>", at = @At("HEAD"), remap = false)
	public void constructorHead(Minecraft minecraft, ClientDist client, ResourceLocation crosshairLocation, CallbackInfo ci) {
		KILL_FEED_MESSAGE_LIFE_MS = 2500;
	}
}
