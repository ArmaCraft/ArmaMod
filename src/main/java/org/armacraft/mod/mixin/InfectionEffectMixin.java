package org.armacraft.mod.mixin;

import com.craftingdead.survival.world.effect.InfectionMobEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(InfectionMobEffect.class)
public class InfectionEffectMixin extends Effect {

    @Shadow @Final private static Random random;

    protected InfectionEffectMixin(EffectType p_i50391_1_, int p_i50391_2_) {
        super(p_i50391_1_, p_i50391_2_);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return random.nextFloat() < 0.025F;
    }

}
