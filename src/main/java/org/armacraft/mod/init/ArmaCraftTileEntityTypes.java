package org.armacraft.mod.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.tileentity.GeneratorTileEntity;

public class ArmaCraftTileEntityTypes {
	// @StringObfuscator:on
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ArmaCraft.MODID);

    public static RegistryObject<TileEntityType<GeneratorTileEntity>> GENERATOR_TILE = TILE_ENTITY_TYPES.register("generator",
            () -> TileEntityType.Builder.of(GeneratorTileEntity::new, ArmaCraftBlocks.GENERATOR.get()).build(null));
	// @StringObfuscator:off
}
