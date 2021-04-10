package org.armacraft.mod.init;

import org.armacraft.mod.GameIntegrity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientDist implements SafeRunnable {
	
    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(ModBlocks.GENERATOR.get(), RenderType.cutout());
    }
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
    	GameIntegrity.INSTANCE.onTick(event);
    }

	@Override
	public void run() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}
}
