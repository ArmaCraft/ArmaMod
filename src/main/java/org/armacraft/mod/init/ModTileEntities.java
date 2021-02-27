package org.armacraft.mod.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.init.ModBlocks;
import org.armacraft.mod.tileentity.GeneratorTileEntity;

public class ModTileEntities {
    public static TileEntityType<GeneratorTileEntity> generatorTileEntityType;

    @SubscribeEvent
    public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
        generatorTileEntityType = TileEntityType.Builder.create(GeneratorTileEntity::new, ModBlocks.generatorBlock).build(null);
        generatorTileEntityType.setRegistryName(ArmaCraft.MODID + ":generator");
        event.getRegistry().register(generatorTileEntityType);
    }
}
