package org.armacraft.mod.mixin;

import java.util.List;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.bridge.IGunItemBridge;
import org.armacraft.mod.wrapper.CommonGunInfoWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mixin(GunItem.class)
public class GunItemMixin implements IGunItemBridge {

	@Mutable @Shadow @Final private int fireDelayMs;
	@Mutable @Shadow @Final private int damage;
	@Mutable @Shadow @Final private int reloadDurationTicks;
	@Mutable @Shadow @Final private float accuracyPct;
	@Mutable @Shadow @Final private int bulletAmountToFire;
	// @StringObfuscator:on
	private static String LORE_HEADSHOT_DAMAGE = "item_lore.gun_item.headshot_damage";
	// @StringObfuscator:off

	/**
	 * Mostra o dano de headshot correto na arma
	 */

	@Inject(method = "appendHoverText", at = @At("TAIL"))
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> lines, ITooltipFlag tooltipFlag,
			CallbackInfo ci) {
		// Cast unsafe mesmo porque sei que sempre será essa a classe
		GunItem gun = (GunItem) (Object) this;
		
		// Remove e adiciona de volta
		lines.remove(3);
		lines.add(3, Text.translate(LORE_HEADSHOT_DAMAGE, 3).withStyle(TextFormatting.GRAY).append(
				Text.of(gun.getGunType().getDamage() * ArmaCraft.ARMACRAFT_HEADSHOT_MULTIPLIER).withStyle(TextFormatting.RED)));
	}

	@Override
	public void bridge$updateSpecs(CommonGunInfoWrapper wrapper) {
		this.fireDelayMs = wrapper.getFireDelayMs();
		this.damage = (int) wrapper.getDamage();
		this.reloadDurationTicks = wrapper.getReloadDurationTicks();
		this.accuracyPct = wrapper.getAccuracyPct();
		this.bulletAmountToFire = wrapper.getBulletAmountToFire();
	}
}
