package org.armacraft.mod.mixin;

import com.craftingdead.core.action.reload.AbstractReloadAction;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractReloadAction.class)
public class AbstractReloadActionMixin {

	/**
	 * Faz com que seja possível recarregar arma enquanto corre
	 */
	@Redirect(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z"))
	public boolean start$isSprinting(LivingEntity entity) {
		return false;
	}

	/**
	 * Faz com que seja possível recarregar arma enquanto corre
	 */
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z"))
	public boolean tick$isSprinting(LivingEntity entity) {
		return false;
	}
}
