package org.armacraft.mod.wrapper;

import com.craftingdead.core.item.GunItem;

public class CommonGunInfoWrapper {
    private String resourceLocation;
    private int fireRateRpm;
    private int fireDelayMs;
    private float damage;
    private int reloadDurationTicks;
    private float accuracyPct;
    private int bulletAmountToFire;

    public static CommonGunInfoWrapper from(GunItem item) {
        return new CommonGunInfoWrapper(item.getRegistryName().toString(),
                item.getGunType().getFireRateRPM(),
                item.getGunType().getFireDelayMs(),
                item.getGunType().getDamage(),
                item.getGunType().getReloadDurationTicks(),
                item.getGunType().getAccuracyPct(),
                item.getGunType().getBulletAmountToFire());
    }

    public CommonGunInfoWrapper(String resourceLocation, int fireRateRpm, int fireDelayMs, float damage, int reloadDurationTicks, float accuracyPct, int bulletAmountToFire) {
        this.resourceLocation = resourceLocation;
        this.fireRateRpm = fireRateRpm;
        this.fireDelayMs = fireDelayMs;
        this.damage = damage;
        this.reloadDurationTicks = reloadDurationTicks;
        this.accuracyPct = accuracyPct;
        this.bulletAmountToFire = bulletAmountToFire;
    }

    public int getFireRateRPM() {
        return fireRateRpm;
    }

    public String getResourceLocation() {
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
}
