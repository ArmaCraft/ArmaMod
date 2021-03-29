package org.armacraft.mod;

import com.craftingdead.core.capability.living.PlayerImpl;
import org.armacraft.mod.init.ModBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.armacraft.mod.init.ModItems;
import org.armacraft.mod.init.ModTileEntityTypes;
import org.armacraft.mod.init.SetupClient;

import java.lang.reflect.Field;

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

        DistExecutor.runWhenOn(Dist.CLIENT, () -> ArmaCraft::registerClientOnlyEvents);
    }

    public static void registerClientOnlyEvents() {
        modEventBus.register(SetupClient.class);
    }

    /*@SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHurt(LivingHurtEvent event) {
        if(event.getSource() == ModDamageSource.DEHYDRATION) {
            event.setCanceled(true);
        }
    }*/

    private void injectWaterDelay() {
        try {
            Field waterTickField = PlayerImpl.class.getDeclaredField("WATER_DELAY_TICKS");
            waterTickField.setAccessible(true);
            waterTickField.set(null, (long) 2400);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
