package org.armacraft.mod.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class MiniSpeedEffect extends Effect {

	protected MiniSpeedEffect() {
		super(EffectType.BENEFICIAL, 0x7CAFC6);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
				"91AEAA56-376B-4498-935B-2F7F68070635", 0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}

}
