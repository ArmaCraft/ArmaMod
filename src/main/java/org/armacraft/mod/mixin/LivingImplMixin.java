package org.armacraft.mod.mixin;

import com.craftingdead.core.living.ILiving;
import com.craftingdead.core.living.ILivingExtension;
import com.craftingdead.core.living.LivingImpl;
import com.craftingdead.core.util.ModSoundEvents;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.GameRules;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Collection;

@Mixin(LivingImpl.class)
public abstract class LivingImplMixin<L extends LivingEntity, E extends ILivingExtension>
        implements ILiving<L, E> {

    /*
     * Desativa deitar
     */
    @Shadow @Final protected Object2ObjectOpenHashMap<ResourceLocation, E> extensions;

    @Shadow public abstract L getEntity();

    @Shadow @Final private ItemStackHandler itemHandler;

    /**
     * @author
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
