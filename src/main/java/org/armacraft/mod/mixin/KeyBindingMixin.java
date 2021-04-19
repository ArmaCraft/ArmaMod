package org.armacraft.mod.mixin;

import org.armacraft.mod.event.DoubleTapKeyBindingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

	private static final long DOUBLE_TAP_MAX_TIMESPAN = 400L;
	private static KeyBinding lastPressedKeyBinding = null;
	private static long lastPressStart = 0L;

	@Inject(method = "setDown", at = @At("HEAD"))
	public void setDown(boolean isDown, CallbackInfo info) {
		if (isDown) {
			KeyBinding self = (KeyBinding) (Object) this;
			boolean wasDown = self.isDown();
			
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
}
