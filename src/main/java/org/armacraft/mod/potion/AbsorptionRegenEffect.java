/*
 * Crafting Dead
 * Copyright (C) 2021  NexusNode LTD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.armacraft.mod.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class AbsorptionRegenEffect extends Effect {

	public static int MAX_ABSORPTION_HEARTS = 20;

	protected AbsorptionRegenEffect() {
		super(EffectType.BENEFICIAL, 0x2552A5);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		// Evita que dê o efeito enquanto morto
		if (livingEntity.isDeadOrDying()) {
			return;
		}

		// Somente server
		if (livingEntity.level.isClientSide()) {
			return;
		}

		// Vida cheia
		if (livingEntity.getHealth() >= livingEntity.getMaxHealth()) {
			final float absorptionAmount = livingEntity.getAbsorptionAmount();

			if (absorptionAmount < MAX_ABSORPTION_HEARTS * 2) {
				livingEntity.setAbsorptionAmount(absorptionAmount + 2);
			}
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int bitShiftedAmplifier = 60 / amplifier; // do vanilla, da potion de regen
		if (bitShiftedAmplifier > 0) {
			return duration % bitShiftedAmplifier == 1; // prefira 1 ao invés de zero aqui
		} else {
			return true;
		}
	}

	@Override
	public void removeAttributeModifiers(LivingEntity livingEntity, AttributeModifierManager attributeManager,
			int amplifier) {
		// Somente server
		if (livingEntity.level.isClientSide()) {
			return;
		}

		// Se o efeito não está mais presente
		if (livingEntity.getEffect(this) == null) {
			// Limpa, senão os corações ficam pra sempre ali
			livingEntity.setAbsorptionAmount(0);
		}
	}
}
