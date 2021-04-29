package org.armacraft.mod.wrapper;

public class ClientGunInfoWrapper {
    private String resourceLocation;
    private int fireRateRpm;
    private int reloadDurationTicks;
    private float accuracyPct;
    private int bulletAmountToFire;

    public ClientGunInfoWrapper(String resourceLocation, int fireRateRpm, int reloadDurationTicks, float accuracyPct, int bulletAmountToFire) {
        this.resourceLocation = resourceLocation;
        this.fireRateRpm = fireRateRpm;
        this.reloadDurationTicks = reloadDurationTicks;
        this.accuracyPct = accuracyPct;
        this.bulletAmountToFire = bulletAmountToFire;
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public int getFireRateRPM() {
        return fireRateRpm;
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
