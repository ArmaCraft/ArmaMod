package org.armacraft.mod.mixin;

import org.armacraft.mod.client.ClientDist;
import org.armacraft.mod.client.util.ClientUtils;
import org.armacraft.mod.event.DoubleTapKeyBindingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

	private static final long DOUBLE_TAP_MAX_TIMESPAN = 400L;
	private static KeyBinding lastPressedKeyBinding = null;
	private static long lastPressStart = 0L;

	@Inject(method = "setDown", at = @At("HEAD"), cancellable = true)
	public void setDown(boolean isDown, CallbackInfo info) {
		
		if (isDown) {
			KeyBinding self = (KeyBinding) (Object) this;
			boolean wasDown = self.isDown();
			
			if (ClientUtils.isAltKeyDown()
					&& ClientDist.get().getUserData() != null
					&& ClientDist.get().getUserData().hasBind(self)) {
				// Solta a tecla, resetando ela.
				this.release();
				// Para por aqui
				info.cancel();
				return;
			}
			
			if (!wasDown) {
				long currentTime = System.currentTimeMillis();
				
				if (lastPressedKeyBinding == self && currentTime - lastPressStart < DOUBLE_TAP_MAX_TIMESPAN) {
					MinecraftForge.EVENT_BUS.post(new DoubleTapKeyBindingEvent(self));
					lastPressStart = 0L;
				} else {
					lastPressStart = currentTime;
				}
			}
			
			lastPressedKeyBinding = self;
		}
	}
	
	@Inject(method = "consumeClick", at = @At("HEAD"))
	public void consumeClick(CallbackInfoReturnable<Void> info) {
		KeyBinding self = (KeyBinding) (Object) this;
		
		if (ClientUtils.isAltKeyDown()
				&& ClientDist.get().getUserData() != null
				&& ClientDist.get().getUserData().hasBind(self)) {
			// Solta a tecla, resetando ela. Evita de contar o clique.
			this.release();
		}
	}
	
	@Shadow private void release() {}
}
