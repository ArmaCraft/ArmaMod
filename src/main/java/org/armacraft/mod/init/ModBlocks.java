package org.armacraft.mod.init;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.block.GeneratorBlock;


public class ModBlocks {
    public static GeneratorBlock generatorBlock;

    @SubscribeEvent
    public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
        generatorBlock = (GeneratorBlock) (new GeneratorBlock().setRegistryName(ArmaCraft.MODID, "generator"));
        blockRegisterEvent.getRegistry().register(generatorBlock);
    }
}
