package org.armacraft.mod.mixin;

import com.craftingdead.virus.entity.monster.AdvancedZombieEntity;
import com.craftingdead.virus.entity.monster.FastZombieEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = FastZombieEntity.class, remap = false)
public class FastZombieEntityMixin {

    /**
     * Comment so pra evitar warning em compilacao (??)
     * @author threader
     * @reason armacraft for the win
     */
    @Overwrite
    public static AttributeModifierMap.MutableAttribute registerAttributes() {
        return AdvancedZombieEntity.registerAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D);
    }
}
