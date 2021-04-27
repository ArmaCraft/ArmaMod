package org.armacraft.mod.wrapper;

public class GunInfoWrapper {
    private String gunResourcePath;
    private int rpm;
    private int reloadDurationTicks;
    private float accuracyPct;
    private int bulletAmountToFire;

    public GunInfoWrapper(String gunResourcePath, int rpm, int reloadDurationTicks, float accuracyPct, int bulletAmountToFire) {
        this.gunResourcePath = gunResourcePath;
        this.rpm = rpm;
        this.reloadDurationTicks = reloadDurationTicks;
        this.accuracyPct = accuracyPct;
        this.bulletAmountToFire = bulletAmountToFire;
    }

    public String getGunResourcePath() {
        return gunResourcePath;
    }

    public int getRpm() {
        return rpm;
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
