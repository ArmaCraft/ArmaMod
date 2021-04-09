package org.armacraft.mod.mixin;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.capability.gun.GunImpl;
import com.craftingdead.core.capability.living.ILiving;
import com.craftingdead.core.item.PaintItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.armacraft.mod.ArmaCraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GunImpl.class)
public class GunImplMixin {

    @Shadow @Final protected ItemStack gunStack;

    @Inject(method = "processShot", remap = false, at = @At("TAIL"))
    private void processShot(ILiving<?, ?> living, CallbackInfo ci) {
        if(living.getEntity() instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) living.getEntity();
            gunStack.getCapability(ModCapabilities.GUN).ifPresent(gunController -> {
                if(gunController.getPaint().isPresent()) {
                    PaintItem paint = (PaintItem) gunController.getPaintStack().getItem();
                    String permissionNode = "armacraft.skins."
                            + gunStack.getItem().getRegistryName().toString().replaceAll("^craftingdead:", "")
                            + "." + paint.getRegistryName().toString().replaceAll("^craftingdead:", "");
                    if (!ArmaCraft.PERMISSION_CHECKER.checkPermission(playerEntity.getUUID(), permissionNode)) {
                        playerEntity.sendMessage(new TranslationTextComponent("message.no_skin_permission")
                                .setStyle(Style.EMPTY.applyFormat(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
                        gunStack.getCapability(ModCapabilities.GUN).ifPresent(x -> x.setPaintStack(ItemStack.EMPTY));
                    }
                }
            });
        }
    }

}
