package org.armacraft.mod.mixin;

import com.craftingdead.core.living.ILiving;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(ILiving.class)
public interface ILivingMixin<T extends LivingEntity> {

    /*
     * Faz mira não abrir ao andar
     */
	@Overwrite(remap = false)
    default float getModifiedAccuracy(float accuracy, Random random) {
		if (this.isCrouching()) {
			accuracy += 0.15F;
		}

		// Se não for player, adiciona um 
		if (!(this.getEntity() instanceof PlayerEntity)) {
			accuracy -= random.nextFloat();
		}

		return accuracy;
    }
    
    @Shadow() boolean isCrouching();
    
    @Shadow() T getEntity();
	
}
