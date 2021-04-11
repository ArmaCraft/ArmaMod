package org.armacraft.mod.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.craftingdead.core.client.renderer.CameraManager;
import com.craftingdead.core.client.renderer.VelocitySmoother;

@Mixin(value = CameraManager.class, remap = false)
public class CameraManagerMixin {

    @Shadow private VelocitySmoother lookPitchSmoother;

    @Shadow private VelocitySmoother lookYawSmoother;

    @Shadow private VelocitySmoother rollSmoother;

    @Shadow private VelocitySmoother pitchSmoother;

    @Shadow @Final private static Random random;

    /**
     * Comment so pra evitar warning em compilacao (??)
     * @author threader
     * @reason armacraft for the win
     */
    @Overwrite
    public void joltCamera(float amountPercent, boolean modifyLookPosition) {
        if (amountPercent == 0.0F) {
            return;
        }
        
        float randomAmount = amountPercent * (random.nextFloat() + 1.0F) / 2.0F;
        float randomNegativeAmount = randomAmount * (random.nextBoolean() ? 1.0F : -1.0F);
        if (modifyLookPosition) {
            this.lookPitchSmoother.add(-randomAmount * 25.0F);
            this.lookYawSmoother.add(randomNegativeAmount * 12.5F);
        }

        this.pitchSmoother.add(-randomAmount * 3.0F);
        this.rollSmoother.add(randomNegativeAmount * 2.5F);
    }
}
