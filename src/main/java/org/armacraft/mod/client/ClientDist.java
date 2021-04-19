package org.armacraft.mod.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.function.LongSupplier;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.ArmaDist;
import org.armacraft.mod.event.DoubleTapKeyBindingEvent;
import org.armacraft.mod.exception.DistNotFoundException;
import org.armacraft.mod.init.ArmaCraftBlocks;
import org.armacraft.mod.network.ClientDashPacket;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;
import org.armacraft.mod.util.MiscUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
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
import net.minecraftforge.fml.network.PacketDistributor;

public class ClientDist implements ArmaDist {

	private static final int MINIMUM_MEMORY_FOR_NOT_JAVA11 = 2500;
	private ClientUserData userData;
	private LongSupplier currentSecond = () -> System.currentTimeMillis() / 1000L;
	private Long lastSecond = currentSecond.getAsLong();
	private int tickCountInTheCurrentSecond = 0;
	private int secondsInViolation = 0;

	private long lastDash = 0L;

	public ClientDist() {
		MinecraftForge.EVENT_BUS.register(this);

		userData = new ClientUserData(new HashSet<>(), new HashSet<>());

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::handleClientSetup);
		modEventBus.addListener(this::handleLoadComplete);
	}

	public void setClientUserData(ClientUserData data) {
		this.userData = data;
	}

	public ClientUserData getClientUserData() {
		return userData;
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

		long maxMB = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
		if (maxMB <= MINIMUM_MEMORY_FOR_NOT_JAVA11) {
			if (!MiscUtil.isUsingJava11()) {
				MiscUtil.runWithoutHeadlessMode(() -> {
					JDialog parentComponent = new JDialog();
					parentComponent.setAlwaysOnTop(true);
					JOptionPane.showMessageDialog(parentComponent,
							"Você NÃO está usando o Java 11 e está usando menos que " + MINIMUM_MEMORY_FOR_NOT_JAVA11
									+ " MB de RAM no modpack. Veja o tutorial para evitar travamentos: https://armacraft.net/ram");
				});
			}
		}
	}
	
	@SubscribeEvent
	public void onDoubleTap(DoubleTapKeyBindingEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		
		if (minecraft.level != null) {
			boolean hasEnoughFood = minecraft.player.getFoodData().getFoodLevel() > 6;
			boolean onGround = minecraft.player.isOnGround();
			boolean notInCooldown = System.currentTimeMillis() - this.lastDash > 550L;
			if (hasEnoughFood && onGround && notInCooldown) {
				if (event.getKeyBinding() == minecraft.options.keyLeft) {
					dash(-90F);
				}
				
				if (event.getKeyBinding() == minecraft.options.keyRight) {
					dash(90F);
				}
				
				if (event.getKeyBinding() == minecraft.options.keyDown) {
					dash(-180F);
				}
			}
		}
	}

	@SubscribeEvent
	public void onNameplateRender(RenderNameplateEvent event) {
		if (event.getEntity() instanceof PlayerEntity){
			ClientUserData data = ArmaCraft.getInstance().getClientDist().orElseThrow(
					() -> new DistNotFoundException("Client")).getClientUserData();
			if(data.getFlags().contains("show-all")) {
				event.setResult(Event.Result.ALLOW);
			} else if (data.getFlags().contains("hide-all")) {
				event.setResult(Event.Result.DENY);
			} else {
				if(!data.getNametagWhitelist().contains(event.getContent().getString())) {
					event.setResult(Event.Result.DENY);
				}
			}
		}
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		// Tela de resourcepacks
		if (event.getGui() instanceof PackScreen) {
			event.setCanceled(true);
		}
	}
	
	private void dash(float angle) {
		Minecraft minecraft = Minecraft.getInstance();
		Vector3d dashMovement = Vector3d.directionFromRotation(0, minecraft.player.yRot + angle).normalize().multiply(0.75F, 0.75F, 0.75F);
		minecraft.player.setDeltaMovement(minecraft.player.getDeltaMovement().add(dashMovement).add(0F, 0.32F, 0F));
		this.lastDash = System.currentTimeMillis();
		minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.HORSE_JUMP, 1.2F, 0.2F));
		
		ArmaCraft.networkChannel.send(PacketDistributor.SERVER.noArg(), new ClientDashPacket());
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		// Somente detectar tick na phase de start
		if (event.phase != Phase.START) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();

		// Player está dentro do mundo do jogo - IMPORTANTE VERIFICAR
		if (minecraft.level != null) {

			// Apenas dois ResourcePacks são instalados por default:
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
				// Segundo não mudou, acrescente
				++this.tickCountInTheCurrentSecond;
			} else {
				// Minecraft roda a 20 ticks por segundo, n�o deveria ser superior, mas
				// existem
				// casos em que pode acontecer, por exemplo,
				// o jogo ou o pc congelar por um tempinho.
				// Testar por 21 ou 22 ticks deve ser o suficiente
				if (this.tickCountInTheCurrentSecond >= 21) {
					this.secondsInViolation++;

					// Está em uma X quantidade de segundos em sequência, sem parar, violando a
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
					// Não está violando, ok, reseta
					this.secondsInViolation = 0;
				}

				// Segundo mudou, começa do zero
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

	@Override
	public void validateUntrustedFolder(FolderSnapshotDTO snapshot, PlayerEntity source) {}
}
