package org.armacraft.mod.mixin;

import com.craftingdead.core.entity.BounceableProjectileEntity;
import com.craftingdead.core.entity.grenade.GrenadeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GrenadeEntity.class)
public abstract class GrenadeEntityMixin extends BounceableProjectileEntity{

    @Shadow public abstract void setActivated(boolean activated);

    @Shadow private int activatedTicksCount;

    public GrenadeEntityMixin(EntityType<? extends BounceableProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setActivated(false);
        this.activatedTicksCount = compound.getInt("activatedTicksCount");
    }
}
