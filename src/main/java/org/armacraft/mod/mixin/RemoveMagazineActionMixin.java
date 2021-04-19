package org.armacraft.mod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.craftingdead.core.action.RemoveMagazineAction;

import net.minecraft.entity.LivingEntity;

@Mixin(RemoveMagazineAction.class)
public class RemoveMagazineActionMixin {

	/**
	 * Faz com que seja possível recarregar arma enquanto corre
	 */
	@Redirect(method = "start", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z"))
	public boolean start$isSprinting(LivingEntity entity) {
		return false;
	}

	/**
	 * Faz com que seja possível recarregar arma enquanto corre
	 */
	@Redirect(method = "tick", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z"))
	public boolean tick$isSprinting(LivingEntity entity) {
		return false;
	}
	
}
