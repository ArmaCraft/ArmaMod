package org.armacraft.mod.mixin;

import java.util.List;

import org.armacraft.mod.clothing.ClothingRepresentation;
import org.armacraft.mod.clothing.ProtectionLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.craftingdead.core.item.ClothingItem;
import com.craftingdead.core.util.Text;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

@Mixin(ClothingItem.class)
public class ClothingItemMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    public void appendHoverText(ItemStack stack, World world,
                             List<ITextComponent> lines,
                             ITooltipFlag tooltipFlag, CallbackInfo ci) {
        if(ClothingRepresentation.has(stack.getItem())) {
            ProtectionLevel level = ClothingRepresentation.getLevel(stack.getItem());
            lines.add(Text.of("Protection Level: " + level.toString()).withStyle(TextFormatting.GRAY));
        }
    }
}
