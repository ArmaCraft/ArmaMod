package org.armacraft.mod.mixin;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.inventory.container.GunCraftSlot;
import com.craftingdead.core.item.AttachmentItem;
import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.item.PaintItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Util;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.server.permission.PermissionAPI;
import org.armacraft.mod.ArmaCraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(GunCraftSlot.class)
public class GunCraftSlotMixin {
    @Shadow
    @Final
    private Inventory craftingInventory;

    /**
     * @author threader
     */
    @Inject(method = "func_190901_a", remap = false, at = @At("TAIL"))
    public void func_190901_a(PlayerEntity playerEntity, ItemStack gunStack, CallbackInfoReturnable<ItemStack> cir) {
        try {
            gunStack.getCapability(ModCapabilities.GUN).ifPresent(gunController -> {
                if(gunController.getPaint().isPresent()) {
                    PaintItem paint = (PaintItem) gunController.getPaintStack().getItem();
                    String permissionNode = "armacraft.skins."
                            + gunStack.getItem().getRegistryName().toString().replaceAll("^craftingdead:", "")
                            + "." + paint.getRegistryName().toString().replaceAll("^craftingdead:", "");
                    System.out.println(permissionNode);
                    if (!ArmaCraft.PERMISSION_CHECKER.checkPermission(playerEntity.getUniqueID(), permissionNode)) {
                        playerEntity.sendMessage(new TranslationTextComponent("message.no_skin_permission")
                                .setStyle(Style.EMPTY.applyFormatting(TextFormatting.RED).setBold(true)), Util.DUMMY_UUID);
                        gunStack.getCapability(ModCapabilities.GUN).ifPresent(x -> x.setPaintStack(ItemStack.EMPTY));
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
