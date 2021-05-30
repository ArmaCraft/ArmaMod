package org.armacraft.mod.mixin;

import com.craftingdead.core.living.IPlayer;
import com.craftingdead.core.living.IPlayerExtension;
import com.craftingdead.core.living.LivingImpl;
import com.craftingdead.core.living.PlayerImpl;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerImpl.class)
public abstract class PlayerImplMixin<L extends PlayerEntity> extends LivingImpl<L, IPlayerExtension>
		implements IPlayer<L> {

	/**
	 * Impede que a perna seja quebrada
	 */
	@Inject(method = "updateBrokenLeg", remap = false, at = @At("HEAD"), cancellable = true)
	public void updateBrokenLeg(CallbackInfo ci) {
		ci.cancel();
	}

	/**
	 * @author
	 */
	@Overwrite(remap = false)
	public void copyFrom(IPlayer<?> that, boolean wasDeath) {
		// Copies the inventory. Doesn't actually matter if it was death or not.
		// Death drops from 'that' should be cleared on death drops to prevent item duplication.
		for (int i = 0; i < 5; i++) {
			this.getItemHandler().setStackInSlot(i, that.getItemHandler().getStackInSlot(i));
		}

		for (IPlayerExtension extension : this.extensions.values()) {
			extension.copyFrom(that, wasDeath);
		}
	}
}
