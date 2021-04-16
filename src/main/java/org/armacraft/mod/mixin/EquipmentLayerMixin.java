package org.armacraft.mod.mixin;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.client.renderer.entity.layer.EquipmentLayer;
import com.craftingdead.core.client.util.RenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.armacraft.mod.ArmaCraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EquipmentLayer.class)
public class EquipmentLayerMixin {

    @Shadow @Final private boolean useHeadOrientation;

    @Shadow @Final private boolean useCrouchingOrientation;

    /**
     * @author
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrix, IRenderTypeBuffer buffers, int packedLight, LivingEntity entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_, CallbackInfo ci) {
        /*if(ArmaCraft.getInstance().getClientDist().getUserData().isClothesHidden()) {
            ci.cancel();
        }*/
    }
}
