package org.armacraft.mod.wrapper;

public class ClientGunDataWrapper {
    private ResourceLocationWrapper resourceLocation;
    private int fireDelayMs;
    private int reloadDurationTicks;
    private float accuracyPct;
    private int bulletAmountToFire;

    public ClientGunDataWrapper(ResourceLocationWrapper resourceLocation, int fireDelayMs, int reloadDurationTicks, float accuracyPct, int bulletAmountToFire) {
        this.resourceLocation = resourceLocation;
        this.fireDelayMs = fireDelayMs;
        this.reloadDurationTicks = reloadDurationTicks;
        this.accuracyPct = accuracyPct;
        this.bulletAmountToFire = bulletAmountToFire;
    }

    public ResourceLocationWrapper getResourceLocation() {
        return resourceLocation;
    }

    public int getFireDelayMs() {
        return fireDelayMs;
    }

    public int getFireRateRPM() {
        return 60000 / fireDelayMs;
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
