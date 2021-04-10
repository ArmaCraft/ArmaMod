package org.armacraft.mod.mixin;

import java.util.List;

import org.armacraft.mod.ArmaCraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.util.Text;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

@Mixin(GunItem.class)
public class GunItemMixin {

	/**
	 * Mostra o dano de headshot correto na arma
	 */

	@Inject(method = "appendHoverText", at = @At("TAIL"))
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> lines, ITooltipFlag tooltipFlag,
			CallbackInfo ci) {
		// Cast unsafe mesmo porque sei que sempre será essa a classe
		GunItem gun = (GunItem) (Object) this;
		
		// Remove e adiciona de volta
		lines.remove(3);
		lines.add(3, Text.translate("item_lore.gun_item.headshot_damage", 3).withStyle(TextFormatting.GRAY).append(
				Text.of(gun.getDamage() * ArmaCraft.ARMACRAFT_HEADSHOT_MULTIPLIER).withStyle(TextFormatting.RED)));
	}
}
