package org.armacraft.mod.mixin;

import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.util.Text;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.server.CustomGunDataController;
import org.armacraft.mod.wrapper.CommonGunDataWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GunItem.class)
public class GunItemMixin {

	@Inject(method = "appendHoverText", at = @At("TAIL"))
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> lines, ITooltipFlag tooltipFlag,
			CallbackInfo ci) {
		//Após eu ter adicionado os headshots multipliers customizados, por algum motivo o forge
		//tem problemas em encontrar esses valores no momento em que esse Mixin é aplicado, portanto
		//eu apenas tiro a lore de headshot e aplico ela pelo próprio Bukkit. É mais fácil.
		lines.remove(3);
	}
}
