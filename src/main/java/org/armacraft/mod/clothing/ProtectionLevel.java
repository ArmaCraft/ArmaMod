package org.armacraft.mod.clothing;

import net.minecraft.util.text.TextFormatting;

public enum ProtectionLevel {
    NONE(1, TextFormatting.DARK_GRAY),
    LOW(0.9f, TextFormatting.RED),
    MEDIUM(0.8f, TextFormatting.BLUE),
    HIGH(0.7f, TextFormatting.GREEN);

	public static float PROTECTION_ENCHANTMENT_MODIFIER = 0.02F;
	public static float FREE_PROTECTION_MODIFIER = 0.07F;
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
