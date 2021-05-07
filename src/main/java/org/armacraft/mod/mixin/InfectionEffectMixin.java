package org.armacraft.mod.mixin;

import com.craftingdead.virus.potion.InfectionEffect;
import com.craftingdead.virus.util.VirusDamageSource;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(InfectionEffect.class)
public class InfectionEffectMixin {

    @Shadow @Final private static Random random;

    @Overwrite
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return random.nextFloat() < 0.05F;
    }

}
