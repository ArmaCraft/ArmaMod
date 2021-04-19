package org.armacraft.mod.potion;

import org.armacraft.mod.ArmaCraft;

import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ArmaCraftEffects {

	public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS,
			ArmaCraft.MODID);

	public static final RegistryObject<Effect> ABSORPTION_REGENERATION = EFFECTS.register("absorption_regeneration",
			AbsorptionRegenEffect::new);

	public static final RegistryObject<Effect> ARMACRAFT_SPEED = EFFECTS.register("mini_speed",
			MiniSpeedEffect::new);
}
