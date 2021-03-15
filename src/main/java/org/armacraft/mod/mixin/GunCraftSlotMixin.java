package org.armacraft.mod.mixin;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.inventory.container.GunCraftSlot;
import com.craftingdead.core.item.AttachmentItem;
import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.item.PaintItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Util;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.server.permission.PermissionAPI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(GunCraftSlot.class)
public class GunCraftSlotMixin {
    @Shadow @Final private Inventory craftingInventory;

    @Inject(at = @At("HEAD"), method = "func_190901_a")
    public void setPaintStack(PlayerEntity playerEntity, ItemStack gunStack, CallbackInfoReturnable<ItemStack> cir) {
        gunStack.getCapability(ModCapabilities.GUN).ifPresent(gunController -> {
            gunController.setPaintStack(ItemStack.EMPTY);
            Set<AttachmentItem> attachments = new HashSet<>();
            for (int i = 0; i < this.craftingInventory.getSizeInventory(); i++) {
                ItemStack itemStack = this.craftingInventory.getStackInSlot(i);
                if (gunController.isAcceptedPaintOrAttachment(itemStack)) {
                    if (itemStack.getItem() instanceof PaintItem) {
                        GunItem gunItem = (GunItem) gunStack.getItem();
                        PaintItem paint = (PaintItem) itemStack.getItem();
                        String permissionNode = "armacraft.skins." + gunItem.getRegistryName().toString() + "." + paint.getRegistryName().toString();
                        if(!PermissionAPI.hasPermission(playerEntity, permissionNode)) {
                            playerEntity.sendMessage(new TranslationTextComponent("message.no_skin_permission")
                                    .setStyle(Style.EMPTY.applyFormatting(TextFormatting.RED).setBold(true)), Util.DUMMY_UUID);
                            return;
                        }
                    }
                }
            }
        });
    }
}
