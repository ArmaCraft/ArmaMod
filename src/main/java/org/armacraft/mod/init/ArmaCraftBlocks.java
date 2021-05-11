package org.armacraft.mod.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.block.GeneratorBlock;

public class ArmaCraftBlocks {
	// @StringObfuscator:on
	public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArmaCraft.MODID);

	public static RegistryObject<GeneratorBlock> GENERATOR = BLOCKS.register("generator", GeneratorBlock::new);
	// @StringObfuscator:off
}
