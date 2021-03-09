package org.armacraft.mod.init;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.LavaFluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.block.GeneratorBlock;


public class ModBlocks {
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArmaCraft.MODID);

    public static RegistryObject<GeneratorBlock> GENERATOR = BLOCKS.register("generator", GeneratorBlock::new);
}
