package org.armacraft.mod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.craftingdead.core.capability.living.PlayerImpl;

@Mixin(PlayerImpl.class)
public class PlayerImplMixin {

	/**
	 * Impede que o player seja infectado
	 */
	@Overwrite(remap = false)
	public void infect(float chance) {
		// Nada
	}
	
	/**
	 * Impede que a perna seja quebrada
	 */
	@Overwrite(remap = false)
	private void updateBrokenLeg() {
		// Nada
	}
}
