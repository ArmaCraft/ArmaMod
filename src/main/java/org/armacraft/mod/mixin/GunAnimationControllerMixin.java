package org.armacraft.mod.mixin;

import org.armacraft.mod.util.GunUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.craftingdead.core.capability.animationprovider.gun.GunAnimationController;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

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
    
    // @formatter:off
    @Shadow public void removeCurrentAnimation() {}
    // @formatter:on
}
