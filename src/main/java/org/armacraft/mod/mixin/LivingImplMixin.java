package org.armacraft.mod.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.craftingdead.core.capability.living.LivingImpl;
import com.craftingdead.core.util.ModSoundEvents;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

@Mixin(LivingImpl.class)
public abstract class LivingImplMixin<T extends LivingEntity> {

    /*
     * Desativa deitar
     */
    @Overwrite(remap = false)
    public void setCrouching(boolean crouching, boolean sendUpdate) {
        // Faz nada
    }
    
	
	/**
	 * Altera o comportamento o som que toca ao trocar de arma pois ele é muito irritante pros jogadores
	 */
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/util/SoundEvent;FF)V"))
    private SoundEvent volumeGunEquip(SoundEvent soundEvent) {
    	if (soundEvent == ModSoundEvents.GUN_EQUIP.get()) {
    		// Toca outro no lugar
            return SoundEvents.ARMOR_EQUIP_LEATHER;
    	}
    	return soundEvent;
    }
	
}
