package org.armacraft.mod.wrapper;

import com.craftingdead.core.item.GunItem;

public class CommonGunDataWrapper {
    private ResourceLocationWrapper resourceLocation;
    private float headshotMultiplier;
    private int fireDelayMs;
    private float damage;
    private int reloadDurationTicks;
    private float accuracyPct;
    private int bulletAmountToFire;

    public static CommonGunDataWrapper of(GunItem gun, float headshotMultiplier) {
        ResourceLocationWrapper resourceLocationWrapper = ResourceLocationWrapper.of(gun.getRegistryName().toString());
        return new CommonGunDataWrapper(
                resourceLocationWrapper,
                gun.getGunType().getFireDelayMs(),
                gun.getGunType().getDamage(),
                gun.getGunType().getReloadDurationTicks(),
                gun.getGunType().getAccuracyPct(),
                gun.getGunType().getBulletAmountToFire(),
                headshotMultiplier
        );
    }

    public CommonGunDataWrapper(ResourceLocationWrapper resourceLocation, int fireDelayMs, float damage, int reloadDurationTicks, float accuracyPct, int bulletAmountToFire, float headshotMultiplier) {
        this.headshotMultiplier = headshotMultiplier;
        this.fireDelayMs = fireDelayMs;
        this.damage = damage;
        this.reloadDurationTicks = reloadDurationTicks;
        this.accuracyPct = accuracyPct;
        this.bulletAmountToFire = bulletAmountToFire;
    }

    public int getFireRateRPM() {
        return 60000 / this.getFireDelayMs();
    }

    public ResourceLocationWrapper getResourceLocation() {
        return resourceLocation;
    }

    public int getFireDelayMs() {
        return fireDelayMs;
    }

    public float getDamage() {
        return damage;
    }

    public int getReloadDurationTicks() {
        return reloadDurationTicks;
    }

    public float getAccuracyPct() {
        return accuracyPct;
    }

    public int getBulletAmountToFire() {
        return bulletAmountToFire;
    }

    public float getHeadshotMultiplier() {
        return headshotMultiplier;
    }
}
