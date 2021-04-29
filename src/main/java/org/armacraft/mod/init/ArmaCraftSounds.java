package org.armacraft.mod.init;

import org.armacraft.mod.ArmaCraft;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ArmaCraftSounds {

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister
			.create(ForgeRegistries.SOUND_EVENTS, ArmaCraft.MODID);

	public static final RegistryObject<SoundEvent> JUMP = register("jump");

	private static RegistryObject<SoundEvent> register(String name) {
		ResourceLocation identifier = new ResourceLocation(ArmaCraft.MODID, name);
		return SOUNDS.register(name, () -> new SoundEvent(identifier));
	}
}
