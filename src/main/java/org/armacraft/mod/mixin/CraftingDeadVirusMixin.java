package org.armacraft.mod.mixin;

import com.craftingdead.core.event.GunEvent;
import com.craftingdead.survival.CraftingDeadSurvival;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CraftingDeadSurvival.class)
public class CraftingDeadVirusMixin {

    /**
     * @author
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void handleGunHitEntity(GunEvent.HitEntity event) {
        //NAO DEIXA O PLAYER SER INFECTADO
    }
}
