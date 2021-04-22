package org.armacraft.mod.mixin;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.network.message.play.TriggerPressedMessage;
import com.craftingdead.core.network.util.NetworkUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;
import org.armacraft.mod.util.GunUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.lang.reflect.Field;
import java.util.function.Supplier;

@Mixin(TriggerPressedMessage.class)
public class TriggerPressedMessageMixin {
    /**
     * @author
     */
    @Overwrite
    public static boolean handle(TriggerPressedMessage msg, Supplier<NetworkEvent.Context> ctx) {
        int entityId = 0;
        boolean triggerPressed = false;
        try {
            Field entityIdField = TriggerPressedMessage.class.getDeclaredField("entityId");
            Field triggerPressedField = TriggerPressedMessage.class.getDeclaredField("triggerPressed");
            entityIdField.setAccessible(true);
            triggerPressedField.setAccessible(true);

            entityId = (int) entityIdField.get(msg);
            triggerPressed = (boolean) triggerPressedField.get(msg);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        boolean finalTriggerPressed = triggerPressed;
        NetworkUtil.getEntity(ctx.get(), entityId)
                .filter(entity -> entity instanceof LivingEntity)
                .ifPresent(entity -> {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    ItemStack heldStack = livingEntity.getMainHandItem();
                    livingEntity.getCapability(ModCapabilities.LIVING)
                            .ifPresent(living -> heldStack
                                    .getCapability(ModCapabilities.GUN)
                                    .ifPresent(gun -> {
                                        if(GunUtils.INTEGRITY_VALIDATOR.test((GunItem)heldStack.getItem())) {
                                            gun.setTriggerPressed(living, finalTriggerPressed,
                                                    ctx.get().getDirection().getReceptionSide().isServer());
                                        }
                                    }));
                });
        return true;
    }
}
