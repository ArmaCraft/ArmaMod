package org.armacraft.mod.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.registry.Registry;

public class EnchantUtils {
    public static boolean hasEnchant(ItemStack stack, Enchantment enchant) {
        return stack.getEnchantmentTags().stream()
                .anyMatch(inbt -> ((CompoundNBT)inbt).getString("id").equalsIgnoreCase(String.valueOf(Registry.ENCHANTMENT.getKey(enchant))));
    }

    public static CompoundNBT getEnchantNBT(ItemStack stack, Enchantment enchant) {
        return (CompoundNBT) stack.getEnchantmentTags().stream()
                .filter(inbt -> ((CompoundNBT)inbt).getString("id").equalsIgnoreCase(String.valueOf(Registry.ENCHANTMENT.getKey(enchant))))
                .findFirst().get();
    }
}
