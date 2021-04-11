package org.armacraft.mod.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.LongSupplier;

import org.armacraft.mod.ArmaCraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientDist implements ArmaDist {

	private LongSupplier currentSecond = () -> System.currentTimeMillis() / 1000L;
	private Long lastSecond = currentSecond.getAsLong();
	private int tickCountInTheCurrentSecond = 0;
	private int secondsInViolation = 0;

	public ClientDist() {
		MinecraftForge.EVENT_BUS.register(this);

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::handleClientSetup);
		modEventBus.addListener(this::handleLoadComplete);
	}

	public void handleClientSetup(FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(ArmaCraftBlocks.GENERATOR.get(), RenderType.cutout());
	}

	public void handleLoadComplete(FMLLoadCompleteEvent event) {
		File icon = new File(getModpackAssetsPath().toFile(), "icon.png");

		// Seta o icone do modpack
		if (icon.exists()) {
			try (InputStream inputStream = new FileInputStream(icon.getAbsoluteFile())) {
				Minecraft.getInstance().getWindow().setIcon(inputStream, inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public void onNameplateRender(RenderNameplateEvent event) {
		if (ArmaCraft.VISIBLE_NAMETAGS == null
				|| !ArmaCraft.VISIBLE_NAMETAGS.contains(event.getContent().getString())) {
			event.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent
	public void onRender(GuiOpenEvent event) {
		// Tela de resourcepacks
		if (event.getGui() instanceof PackScreen) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		// Somente detectar tick na phase de start
		if (event.phase != Phase.START) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();

		// Player est� dentro do mundo do jogo - IMPORTANTE VERIFICAR
		if (minecraft.level != null) {

			// Apenas dois ResourcePacks s�o instalados por default:
			// "vanilla" e "mod_resources"
			if (minecraft.getResourcePackRepository().getSelectedPacks().size() != 2) {
				// Envia um comando pra avisar os staffers
				minecraft.player.chat("/clientmessage has-additional-resourcepack");

				// Fecha o jogo
				minecraft.close();
				return;
			}

			final long currentSecond = this.currentSecond.getAsLong();

			if (this.lastSecond == currentSecond) {
				// Segundo n�o mudou, acrescente
				++this.tickCountInTheCurrentSecond;
			} else {
				// Minecraft roda a 20 ticks por segundo, n�o deveria ser superior, mas
				// existem
				// casos em que pode acontecer, por exemplo,
				// o jogo ou o pc congelar por um tempinho.
				// Testar por 21 ou 22 ticks deve ser o suficiente
				if (this.tickCountInTheCurrentSecond >= 21) {
					this.secondsInViolation++;

					// Est� em uma X quantidade de segundos em sequ�ncia, sem parar, violando a
					// velocidade de tick
					if (this.secondsInViolation > 10) {
						try {
							// Envia um comando pra avisar os staffers
							minecraft.player.chat("/clientmessage too-high-tps");

							// Congela o jogo
							Thread.sleep(99999999L);
						} catch (Exception e) {
							minecraft.close();
						}
					}
				} else {
					// N�o est� violando, ok, reseta
					this.secondsInViolation = 0;
				}

				// Segundo mudou, come�a do zero
				this.tickCountInTheCurrentSecond = 0;
			}

			this.lastSecond = currentSecond;
		}
	}

	private static Path getModpackAssetsPath() {
		// Get the current Working directory
		Path currentRelativePath = Paths.get("").toAbsolutePath();

		Path modpacksPath = currentRelativePath.getParent();
		if (modpacksPath == null) {
			return currentRelativePath;
		}

		// Should be the .technic directory
		Path technicPath = modpacksPath.getParent();
		if (technicPath == null) {
			return currentRelativePath;
		}

		// Should be the asset directory for that modpack
		return Paths.get(technicPath.toAbsolutePath().toString(), "assets", "packs", "armacraft-reborn");
	}
}
