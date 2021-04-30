package org.armacraft.mod.mixin;

import org.armacraft.mod.server.ServerDist;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.inventory.container.GunCraftSlot;
import com.craftingdead.core.item.PaintItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

@Mixin(GunCraftSlot.class)
public class GunCraftSlotMixin {
    @Shadow
    @Final
    private Inventory craftingInventory;

    /**
     * @author threader
     */
    @Inject(method = "onTake", at = @At("TAIL"))
    public void onTake(PlayerEntity playerEntity, ItemStack gunStack, CallbackInfoReturnable<ItemStack> cir) {
        try {
            gunStack.getCapability(ModCapabilities.GUN).ifPresent(gunController -> {
                if(gunController.getPaint().isPresent()) {
                    PaintItem paint = (PaintItem) gunController.getPaintStack().getItem();
                    String permissionNode = "armacraft.skins."
                            + gunStack.getItem().getRegistryName().getPath() + "." + paint.getRegistryName().getPath();
                    if (!ServerDist.PERMISSION_BRIDGE.hasPermission(playerEntity.getUUID(), permissionNode)) {
                        playerEntity.sendMessage(new TranslationTextComponent("message.no_skin_permission")
                                .setStyle(Style.EMPTY.applyFormat(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
                        gunStack.getCapability(ModCapabilities.GUN).ifPresent(x -> x.setPaintStack(ItemStack.EMPTY));
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
