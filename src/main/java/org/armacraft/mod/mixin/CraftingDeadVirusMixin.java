package org.armacraft.mod.mixin;

import com.craftingdead.virus.CraftingDeadVirus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingDeadVirus.class)
public class CraftingDeadVirusMixin {

    /**
     * Impede que o player seja infectado
     *
     * protected pois o Mixins não permite Inject em métodos public static.
     */
    @Inject(method = "handleLivingAttack", at = @At("HEAD"), remap = false, cancellable = true)
    public void handleLivingAttack(LivingAttackEvent event, CallbackInfo ci) {
        ci.cancel();
    }

}
