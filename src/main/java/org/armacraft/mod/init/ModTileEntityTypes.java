package org.armacraft.mod.init;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.init.ModBlocks;
import org.armacraft.mod.tileentity.GeneratorTileEntity;

public class ModTileEntityTypes {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ArmaCraft.MODID);

    public static RegistryObject<TileEntityType<GeneratorTileEntity>> GENERATOR_TILE = TILE_ENTITY_TYPES.register("generator",
            () -> TileEntityType.Builder.create(GeneratorTileEntity::new, ModBlocks.GENERATOR.get()).build(null));
}
