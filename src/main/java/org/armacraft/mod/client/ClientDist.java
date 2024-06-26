package org.armacraft.mod.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.ArmaDist;
import org.armacraft.mod.bridge.bukkit.IUserData;
import org.armacraft.mod.client.util.ClientUtils;
import org.armacraft.mod.event.DoubleTapKeyBindingEvent;
import org.armacraft.mod.init.ArmaCraftBlocks;
import org.armacraft.mod.init.ArmaCraftSounds;
import org.armacraft.mod.network.ClientDashPacket;
import org.armacraft.mod.network.ClientOpenedCheatEnginePacket;
import org.armacraft.mod.network.KeybindingsUpdatePacket;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;
import org.armacraft.mod.util.Cooldown;
import org.armacraft.mod.util.MiscUtil;
import org.armacraft.mod.wrapper.EnvironmentWrapper;
import org.armacraft.mod.wrapper.ProcessWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.function.LongSupplier;

public class ClientDist implements ArmaDist {
	
	private static final int MINIMUM_MEMORY_FOR_NOT_JAVA11 = 2500;
	private IUserData userData;
	private LongSupplier currentSecond = () -> System.currentTimeMillis() / 1000L;
	private Long lastSecond = currentSecond.getAsLong();
	private int tickCountInTheCurrentSecond = 0;
	private int secondsInViolation = 0;
	private int tickCountToDetectCheatEngine = 0;
	private final List<FolderSnapshotDTO> firstSnapshot;

	private long lastDash = 0L;

	public ClientDist() {
		this.firstSnapshot = ImmutableList.copyOf(ClientRiskyGameFolder.createSnapshotsOfAllRiskyFolders());
		
		ClientUtils.deleteArmaModJarFile();
		this.userData = new ClientUserData();
		checkAndWarnAboutRAM();
		
		MinecraftForge.EVENT_BUS.register(this);

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::handleClientSetup);
		modEventBus.addListener(this::handleLoadComplete);
	}
	
	private boolean isDashInCooldown() {
		return System.currentTimeMillis() - this.lastDash <= 550L;
	}
	
	private boolean isGameWorldLoaded() {
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft.level != null;
	}
	
	private boolean isPlayerInWorld() {
		Minecraft minecraft = Minecraft.getInstance();
		return this.isGameWorldLoaded() && minecraft.player != null;
	}
	
	private void dash(float angle) {
		if (!this.canDash()) {
			return;
		}
		
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayerEntity player = minecraft.player;
		BlockState blockBelowFeet = MiscUtil.getBlockBelowFeet(player);
		
		// Avisa o server de que eu dei dash
		ArmaCraft.networkChannel.send(PacketDistributor.SERVER.noArg(), new ClientDashPacket());
		
		Vector3d dashMovement = Vector3d.directionFromRotation(0, player.yRot + angle).normalize().multiply(0.75F, 0.75F, 0.75F).add(0F, 0.32F, 0F);
		player.setDeltaMovement(player.getDeltaMovement().add(dashMovement));
		ClientUtils.playLocalSound(ArmaCraftSounds.JUMP.get(),  MiscUtil.randomFloat(0.9F, 1.0F), 0.125F);

		// Particula de dash
		for (int i = 0; i < 10; i++) {
			minecraft.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockBelowFeet), true, player.getX(), player.getY() + 0.1D, player.getZ(), 0.0D, 0.0075D, 0.0D);
		}

		this.lastDash = System.currentTimeMillis();
	}

	public IUserData getUserData() {
		if(this.userData == null) {
			this.userData = new ClientUserData();
		}
		return this.userData;
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
	
	@SubscribeEvent()
	public void onServerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
		// Limpa tudo
		this.userData = null;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void handleKeyInput(InputEvent.KeyInputEvent event) {
		// @StringObfuscator:on
		Minecraft minecraft = Minecraft.getInstance();

		if (this.isPlayerInWorld()) {
			if (ClientUtils.isAltKeyDown()) {
				if(userData != null){
					this.userData.getKeyBinds().forEach((keybind) -> {
						if (ClientUtils.isKeyDown(keybind.getBind())) {
							if (!Cooldown.checkAndPut("keybind", 500L)) {
								ClientUtils.playLocalSound(SoundEvents.UI_BUTTON_CLICK, 1.2F, 1F);
								minecraft.player.chat("/" + keybind.getCommand());
							}
						}
					});
				}
			}
		}
		// @StringObfuscator:off
	}
	
	private boolean canDash() {
		Minecraft minecraft = Minecraft.getInstance();
		
		boolean hasEnoughFood = minecraft.player.getFoodData().getFoodLevel() > 6;
		boolean onGround = minecraft.player.isOnGround();
		boolean notInCooldown = !this.isDashInCooldown();
		return hasEnoughFood && onGround && notInCooldown;
	}
	
	@SubscribeEvent
	public void onDoubleTap(DoubleTapKeyBindingEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		
		if (this.isPlayerInWorld()) {
			if (this.canDash()) {
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
		if (event.getEntity() instanceof PlayerEntity && this.userData != null) {
			PlayerEntity player = (PlayerEntity) event.getEntity();
			if(Minecraft.getInstance().crosshairPickEntity != null){
				Entity entity = Minecraft.getInstance().crosshairPickEntity;
				if(entity instanceof PlayerEntity && entity.getId() == event.getEntity().getId()) {
					event.setResult(Event.Result.ALLOW);
					return;
				}
			}
			if(userData.getFlags().contains(IUserData.Flags.NAMETAGS_SHOW_ALL)) {
				event.setResult(Event.Result.ALLOW);
			} else if (userData.getFlags().contains(IUserData.Flags.NAMETAGS_HIDE_ALL)) {
				event.setResult(Event.Result.DENY);
			} else {
				if(!userData.getNametagWhitelist().contains(player.getName().getString())) {
					event.setResult(Event.Result.DENY);
				} else {
					event.setResult(Event.Result.ALLOW);
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

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		// Somente detectar tick na phase de start
		if (event.phase != Phase.START) {
			return;
		}

		//Fecha jogo se o java estiver no modo debugger
		if(isJavaInDebugMode()) {
			//ClientUtils.silentlyMakeGameStop();
		}
		
		if (++this.tickCountToDetectCheatEngine % 20 == 0) {
			final boolean isCheatEngineOpen = this.getEnvironment().getRunningProcesses().stream()
					.anyMatch(process -> process.getMainWindowTitle().toLowerCase().contains("cheat engine"));
			if (isCheatEngineOpen) {
				if(isPlayerInWorld()) {
					ArmaCraft.networkChannel.send(PacketDistributor.SERVER.noArg(), new ClientOpenedCheatEnginePacket());
				}
				ClientUtils.silentlyMakeGameStop();
			}
		}

		Minecraft minecraft = Minecraft.getInstance();

		// Player está dentro do mundo do jogo - IMPORTANTE VERIFICAR
		if (minecraft.level != null) {

			// Apenas dois ResourcePacks são instalados por default:
			// "vanilla" e "mod_resources"
			if (minecraft.getResourcePackRepository().getSelectedPacks().size() != 2) {
				// Envia um comando pra avisar os staffers
				// @StringObfuscator:on
				minecraft.player.chat("/clientmessage has-additional-resourcepack");
				// @StringObfuscator:off

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
							// @StringObfuscator:on
							minecraft.player.chat("/clientmessage too-high-tps");
							// @StringObfuscator:off

							// Congela o jogo e depois fecha
							ClientUtils.freezeGameAndExit(99999999);
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
		// @StringObfuscator:on
		return Paths.get(technicPath.toAbsolutePath().toString(), "assets", "packs", "armacraft-reborn");
		// @StringObfuscator:off
	}
	
	public List<FolderSnapshotDTO> getFirstSnapshot() {
		return this.firstSnapshot;
	}

	@Override
	public EnvironmentWrapper getEnvironment() {
		String osName = System.getProperty("os.name");
		String java = System.getProperty("java.version");
		Set<ProcessWrapper> runningProcesses = ProcessesLookupService.INSTANCE.getCurrentProcesses();

		return new EnvironmentWrapper(osName, java, runningProcesses);
	}

	@Override
	public boolean validateClassesHash(String hash, PlayerEntity source) {return true;}

	@Override
	public void validateUntrustedFolders(List<FolderSnapshotDTO> snapshot, PlayerEntity source) {}

	@Override
	public void validateTransformationServices(List<String> transformationServices, PlayerEntity source) {}
	
	public static ClientDist get() {
		return ArmaCraft.getInstance().getClientDist().get();
	}
	
	public static void checkAndWarnAboutRAM() {
		long maxMB = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
		if (maxMB <= MINIMUM_MEMORY_FOR_NOT_JAVA11) {
			if (!MiscUtil.isUsingJava11()) {
				// @StringObfuscator:on
				ClientUtils.openFrameWith("AVISO", "<html><body width='600'><center>Você NÃO está usando o Java 11 e está usando menos que " + MINIMUM_MEMORY_FOR_NOT_JAVA11
									+ " MB de RAM no modpack.<br>Use nosso instalador do Java 11 para Technic: https://armacraft.net/java<br><br>Você pode continuar jogando com seu Java atual, mas se não aumentar sua RAM<br>ou se não mudar para o Java 11, seu jogo poderá ficar meio lagado.<br>Dado o aviso, você pode continuar jogando como está, se quiser.</center>");
				// @StringObfuscator:off
			}
		}
	}
}
