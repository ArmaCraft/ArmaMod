package org.armacraft.mod.mixin;

import com.craftingdead.core.client.renderer.entity.layer.EquipmentLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EquipmentLayer.class)
public class EquipmentLayerMixin {

    @Shadow @Final private boolean useHeadOrientation;

    @Shadow @Final private boolean useCrouchingOrientation;

    /**
     * @author
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrix, IRenderTypeBuffer buffers, int packedLight, LivingEntity entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_, CallbackInfo ci) {
        /*if(ArmaCraft.getInstance().getClientDist().getUserData().isClothesHidden()) {
            ci.cancel();
        }
    }*/
}
