package org.armacraft.mod.mixin;

import com.craftingdead.core.item.animation.gun.GunAnimationController;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.armacraft.mod.util.GunUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GunAnimationController.class)
public class GunAnimationControllerMixin {

	/**
	 * Remove tremedeira da arma enquanto atira mirando
	 */
	
    @Inject(method = "tick", remap = false, at = @At("HEAD"))
    public void tick(LivingEntity livingEntity, ItemStack itemStack, CallbackInfo ci) {
    	if (GunUtils.isAiming(livingEntity)) {
    		this.removeCurrentAnimation();
    	}
    }
    
    @Shadow public void removeCurrentAnimation() {}
}
