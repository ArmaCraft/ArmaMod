package org.armacraft.mod;

import org.armacraft.mod.init.ModBlocks;
import org.armacraft.mod.init.ModItems;
import org.armacraft.mod.init.ModTileEntityTypes;
import org.armacraft.mod.init.ClientDist;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ArmaCraft.MODID)
public class ArmaCraft {

    public static final String MODID = "armacraft";
    public static PermissionChecker PERMISSION_CHECKER;
    public static IEventBus modEventBus;


    public ArmaCraft() {
        modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        this.injectWaterDelay();

        modEventBus.register(this);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientDist::new);
    }

    /*@SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHurt(LivingHurtEvent event) {
        if(event.getSource() == ModDamageSource.DEHYDRATION) {
            event.setCanceled(true);
        }
    }*/

    private void injectWaterDelay() {
    	/*
    	 * Não funciona porque campos STATIC FINAL primitivos são copiados e colados durante o tempo de compilacao da jar
    	 * 
        try {
            Field waterTickField = PlayerImpl.class.getDeclaredField("WATER_DELAY_TICKS");
            waterTickField.setAccessible(true);
            waterTickField.setInt(null, 2400);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        */
    }
}
