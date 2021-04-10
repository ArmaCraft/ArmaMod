package org.armacraft.mod.clothing;

import net.minecraft.util.text.TextFormatting;

public enum ProtectionLevel {
    NONE(1, TextFormatting.DARK_GRAY),
    LOW(0.9f, TextFormatting.RED),
    MEDIUM(0.8f, TextFormatting.BLUE),
    HIGH(0.7f, TextFormatting.GREEN);

    public static final float PROTECTION_BASE_MULTIPLIER = 1.05f;
    public static final float PROTECTION_MULTIPLIER = 0.05f;
    private final float standardProtection;
    private final TextFormatting color;

    ProtectionLevel(float standardProtection, TextFormatting color) {
        this.standardProtection = standardProtection;
        this.color = color;
    }

    public float getProtection() {
        return standardProtection;
    }
}
