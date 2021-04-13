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
import net.minecraft.util.math.MathHelper;

public class AbsorptionRegenEffect extends Effect {

	public static int MAX_ABSORPTION_HEARTS = 20 * 2;
	public static int DEFAULT_MINIMUM_HEARTS_ON_EAT = 3 * 2;

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

		// Vida quase cheia
		if (livingEntity.getHealth() >= (livingEntity.getMaxHealth() - 0.5F)) {
			final float absorptionAmount = livingEntity.getAbsorptionAmount();

			livingEntity.setAbsorptionAmount(MathHelper.clamp(absorptionAmount + 2, 0, MAX_ABSORPTION_HEARTS));
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
	public void addAttributeModifiers(LivingEntity livingEntity, AttributeModifierManager attributeManager,
			int amplifier) {
		// Somente server
		if (livingEntity.level.isClientSide()) {
			return;
		}

		if (livingEntity.getAbsorptionAmount() < DEFAULT_MINIMUM_HEARTS_ON_EAT) {
			livingEntity.setAbsorptionAmount(DEFAULT_MINIMUM_HEARTS_ON_EAT);
		}
	}

	@Override
	public void removeAttributeModifiers(LivingEntity livingEntity, AttributeModifierManager attributeManager,
			int amplifier) {
		// Somente server
		if (livingEntity.level.isClientSide()) {
			return;
		}

		// N�o sei porque, mas isso evita que todos os meus corações voltem pra zero se eu receber o efeito por /effect ou por plugin
		if (!livingEntity.hasEffect(this)) {
			// Limpa, sen�o os corações ficam pra sempre ali
			livingEntity.setAbsorptionAmount(0);
		}
	}
}
