package org.armacraft.mod;

import org.armacraft.mod.init.ModBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.armacraft.mod.init.ModItems;
import org.armacraft.mod.init.ModTileEntityTypes;
import org.armacraft.mod.init.SetupClient;

@Mod(ArmaCraft.MODID)
public class ArmaCraft {

    public static final String MODID = "armacraft";
    public static IEventBus modEventBus;

    public ArmaCraft() {
        modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> ArmaCraft::registerClientOnlyEvents);

    }

    public static void registerClientOnlyEvents() {
        modEventBus.register(SetupClient.class);
    }
}
