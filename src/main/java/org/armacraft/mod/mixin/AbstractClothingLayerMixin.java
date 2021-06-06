package org.armacraft.mod.mixin;

import com.craftingdead.core.client.renderer.entity.layer.AbstractClothingLayer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractClothingLayer.class)
public abstract class AbstractClothingLayerMixin<T extends LivingEntity, M extends BipedModel<T>>
        extends LayerRenderer<T, M> {

    public AbstractClothingLayerMixin(IEntityRenderer<T, M> p_i50926_1_) {
        super(p_i50926_1_);
    }

    @Override


}
