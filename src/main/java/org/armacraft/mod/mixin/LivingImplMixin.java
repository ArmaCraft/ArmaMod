package org.armacraft.mod.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.craftingdead.core.capability.living.LivingImpl;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(LivingImpl.class)
public abstract class LivingImplMixin<T extends LivingEntity> {

    /*
     * Desativa deitar
     */
    @Overwrite(remap = false)
    public void setCrouching(boolean crouching, boolean sendUpdate) {
        // Faz nada
    }
}
