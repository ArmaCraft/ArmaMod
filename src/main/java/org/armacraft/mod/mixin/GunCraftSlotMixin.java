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
    @Inject(method = "func_190901_a", remap = false, at = @At("HEAD"))
    public void func_190901_a(PlayerEntity playerEntity, ItemStack gunStack, CallbackInfoReturnable<ItemStack> cir) {
        try {
            System.out.println("1");
            if(gunStack.getItem() instanceof GunItem) {
                System.out.println("2");
                if (!craftingInventory.getStackInSlot(3).isEmpty()) {
                    System.out.println("3");
                    ItemStack stack = craftingInventory.getStackInSlot(3);
                    PaintItem paint = (PaintItem) stack.getItem();
                    String permissionNode = "armacraft.skins."
                            + gunStack.getItem().getRegistryName().toString().replaceAll("^armacraft:", "")
                            + "." + paint.getRegistryName().toString().replaceAll("^armacraft:", "");
                    System.out.println("4");
                    if (!ArmaCraft.PERMISSION_CHECKER.checkPermission(playerEntity.getUniqueID(), permissionNode)) {
                        System.out.println("4");
                        playerEntity.sendMessage(new TranslationTextComponent("message.no_skin_permission")
                                .setStyle(Style.EMPTY.applyFormatting(TextFormatting.RED).setBold(true)), Util.DUMMY_UUID);
                        return;
                    }
                    System.out.println("5");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
