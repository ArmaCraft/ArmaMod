package org.armacraft.mod.mixin;

import com.craftingdead.core.item.gun.AbstractGunType;
import org.armacraft.mod.bridge.IAbstractGunTypeBridge;
import org.armacraft.mod.wrapper.CommonGunDataWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractGunType.class)
public class AbstractGunTypeMixin implements IAbstractGunTypeBridge {

    @Mutable @Shadow @Final private int fireDelayMs;
    @Mutable @Shadow @Final private int damage;
    @Mutable @Shadow @Final private int reloadDurationTicks;
    @Mutable @Shadow @Final private float accuracyPct;
    @Mutable @Shadow @Final private int bulletAmountToFire;

    @Override
	public void bridge$updateSpecs(CommonGunDataWrapper wrapper) {
        this.fireDelayMs = wrapper.getFireDelayMs();
        this.damage = (int) wrapper.getDamage();
        this.reloadDurationTicks = wrapper.getReloadDurationTicks();
        this.accuracyPct = wrapper.getAccuracyPct();
        this.bulletAmountToFire = wrapper.getBulletAmountToFire();
    }

}
