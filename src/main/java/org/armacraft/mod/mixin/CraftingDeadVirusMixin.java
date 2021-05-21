package org.armacraft.mod.mixin;

import com.craftingdead.core.event.GunEvent;
import com.craftingdead.virus.CraftingDeadVirus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CraftingDeadVirus.class)
public class CraftingDeadVirusMixin {

    @Overwrite(remap = false)
    @SubscribeEvent
    public void handleGunHitEntity(GunEvent.HitEntity event) {
        //NAO DEIXA O PLAYER SER INFECTADO
    }
}
