package org.armacraft.mod.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.registry.Registry;

public class EnchantUtils {
	public static boolean hasEnchant(ItemStack stack, Enchantment enchant) {
		return stack.getEnchantmentTags().stream().anyMatch(inbt -> ((CompoundNBT) inbt).getString("id")
				.equalsIgnoreCase(String.valueOf(Registry.ENCHANTMENT.getKey(enchant))));
	}

	public static CompoundNBT getEnchantNBT(ItemStack stack, Enchantment enchant) {
		return (CompoundNBT) stack.getEnchantmentTags().stream().filter(inbt -> ((CompoundNBT) inbt).getString("id")
				.equalsIgnoreCase(String.valueOf(Registry.ENCHANTMENT.getKey(enchant)))).findFirst().get();
	}

	public static int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
		if (stack.isEmpty()) {
			return 0;
		}

		if (!EnchantUtils.hasEnchant(stack, enchantment)) {
			return 0;
		}

		return EnchantUtils.getEnchantNBT(stack, Enchantments.PROJECTILE_PROTECTION).getInt("lvl");
	}
}
