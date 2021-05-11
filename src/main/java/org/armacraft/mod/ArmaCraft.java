package org.armacraft.mod;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.event.GunEvent;
import com.craftingdead.core.inventory.InventorySlotType;
import com.craftingdead.core.item.ModItems;
import com.craftingdead.core.item.PaintItem;
import com.craftingdead.core.item.gun.AbstractGun;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.armacraft.mod.client.ClientDist;
import org.armacraft.mod.clothing.ClothingRepresentation;
import org.armacraft.mod.clothing.ProtectionLevel;
import org.armacraft.mod.init.ArmaCraftBlocks;
import org.armacraft.mod.init.ArmaCraftItems;
import org.armacraft.mod.init.ArmaCraftSounds;
import org.armacraft.mod.init.ArmaCraftTileEntityTypes;
import org.armacraft.mod.network.ClientDashPacket;
import org.armacraft.mod.network.ClientEnvironmentRequestPacket;
import org.armacraft.mod.network.ClientEnvironmentResponsePacket;
import org.armacraft.mod.network.ClientGunInfoPacket;
import org.armacraft.mod.network.ClientInfoRequestPacket;
import org.armacraft.mod.network.ClientInfoResponsePacket;
import org.armacraft.mod.network.ClientOpenedCheatEnginePacket;
import org.armacraft.mod.network.CloseGamePacket;
import org.armacraft.mod.network.CommonGunSpecsUpdatePacket;
import org.armacraft.mod.network.UpdateUserDataPacket;
import org.armacraft.mod.potion.ArmaCraftEffects;
import org.armacraft.mod.server.CustomGunDataController;
import org.armacraft.mod.server.ServerDist;
import org.armacraft.mod.util.EnchantUtils;
import org.armacraft.mod.util.MiscUtil;
import org.armacraft.mod.wrapper.CommonGunDataWrapper;

import java.util.Optional;

@Mod(ArmaCraft.MODID)
public class ArmaCraft {

	// Infelizmente não tem como ofuscar esse field
	public static final String MODID = "armacraft";
	// @StringObfuscator:on
	private static final String NETWORK_VERSION = "@NETWORK_VERSION@";
	// @StringObfuscator:off
	
	private static ArmaCraft instance;

	public static float DEFAULT_HEADSHOT_MULTIPLIER = 1.5F;

	public static final SimpleChannel networkChannel = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(ArmaCraft.MODID, "play")).clientAcceptedVersions(version -> true)
			.serverAcceptedVersions(NETWORK_VERSION::equals).networkProtocolVersion(() -> NETWORK_VERSION)
			.simpleChannel();

	private ArmaDist dist;

	public ArmaCraft() {
		instance = this;

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::handleCommonSetup);
		modEventBus.addListener(this::handleDedicatedSetup);

		MinecraftForge.EVENT_BUS.register(this);

	    ArmaCraftSounds.SOUNDS.register(modEventBus);
		ArmaCraftBlocks.BLOCKS.register(modEventBus);
		ArmaCraftItems.ITEMS.register(modEventBus);
		ArmaCraftEffects.EFFECTS.register(modEventBus);
		ArmaCraftTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

		this.dist = DistExecutor.safeRunForDist(() -> ClientDist::new, () -> ServerDist::new);

		this.setupChannel();
	}

	public void setupChannel() {
		int packetId = -1;
		
		networkChannel.messageBuilder(ClientInfoRequestPacket.class, ++packetId, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientInfoRequestPacket::encode)
				.decoder(ClientInfoRequestPacket::decode)
				.consumer(ClientInfoRequestPacket::handle).add();

		networkChannel.messageBuilder(ClientInfoResponsePacket.class, ++packetId, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ClientInfoResponsePacket::encode)
				.decoder(ClientInfoResponsePacket::decode)
				.consumer(ClientInfoResponsePacket::handle).add();

		networkChannel.messageBuilder(UpdateUserDataPacket.class, ++packetId, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(UpdateUserDataPacket::encode)
				.decoder(UpdateUserDataPacket::decode)
				.consumer(UpdateUserDataPacket::handle).add();

		networkChannel.messageBuilder(ClientDashPacket.class, ++packetId, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ClientDashPacket::encode)
				.decoder(ClientDashPacket::decode)
				.consumer(ClientDashPacket::handle).add();

		networkChannel.messageBuilder(UpdateUserDataPacket.class, ++packetId, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(UpdateUserDataPacket::encode)
				.decoder(UpdateUserDataPacket::decode)
				.consumer(UpdateUserDataPacket::handle).add();

		networkChannel.messageBuilder(ClientEnvironmentResponsePacket.class, ++packetId, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ClientEnvironmentResponsePacket::encode)
				.decoder(ClientEnvironmentResponsePacket::decode)
				.consumer(ClientEnvironmentResponsePacket::handle).add();

		networkChannel.messageBuilder(ClientEnvironmentRequestPacket.class, ++packetId, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientEnvironmentRequestPacket::encode)
				.decoder(ClientEnvironmentRequestPacket::decode)
				.consumer(ClientEnvironmentRequestPacket::handle).add();

		networkChannel.messageBuilder(ClientGunInfoPacket.class, ++packetId, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ClientGunInfoPacket::encode)
				.decoder(ClientGunInfoPacket::decode)
				.consumer(ClientGunInfoPacket::handle).add();

		/*networkChannel.messageBuilder(ClientClassesHashResponsePacket.class, ++packetId, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ClientClassesHashResponsePacket::encode)
				.decoder(ClientClassesHashResponsePacket::decode)
				.consumer(ClientClassesHashResponsePacket::handle).add();

		networkChannel.messageBuilder(ClientClassesHashRequestPacket.class, ++packetId, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientClassesHashRequestPacket::encode)
				.decoder(ClientClassesHashRequestPacket::decode)
				.consumer(ClientClassesHashRequestPacket::handle).add();*/

		networkChannel.messageBuilder(CloseGamePacket.class, ++packetId, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(CloseGamePacket::encode)
				.decoder(CloseGamePacket::decode)
				.consumer(CloseGamePacket::handle).add();

		networkChannel.messageBuilder(ClientOpenedCheatEnginePacket.class, ++packetId, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ClientOpenedCheatEnginePacket::encode)
				.decoder(ClientOpenedCheatEnginePacket::decode)
				.consumer(ClientOpenedCheatEnginePacket::handle).add();


		networkChannel.messageBuilder(CommonGunSpecsUpdatePacket.class, ++packetId, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(CommonGunSpecsUpdatePacket::encode)
				.decoder(CommonGunSpecsUpdatePacket::decode)
				.consumer(CommonGunSpecsUpdatePacket::handle).add();
	}

	public void handleCommonSetup(FMLCommonSetupEvent event) {
		this.registerClothings();
	}

	public void handleDedicatedSetup(FMLDedicatedServerSetupEvent event) {
		CustomGunDataController.INSTANCE.populateDefaultData();
	}

	private void registerClothings() {
		ClothingRepresentation.register(ModItems.ARMY_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.SAS_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.SPETSNAZ_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.POLICE_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.CAMO_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.COMBAT_BDU_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.WINTER_ARMY_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.ARMY_DESERT_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.PILOT_CLOTHING.get(), ProtectionLevel.MEDIUM);
		ClothingRepresentation.register(ModItems.HAZMAT_CLOTHING.get(), ProtectionLevel.MEDIUM);
		ClothingRepresentation.register(ModItems.TAC_GHILLIE_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.SWAT_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.SPACE_SUIT_CLOTHING.get(), ProtectionLevel.MEDIUM);
		ClothingRepresentation.register(ModItems.SHERIFF_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.JUGGERNAUT_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.FIREMAN_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.DOCTOR_CLOTHING.get(), ProtectionLevel.NONE);
		ClothingRepresentation.register(ModItems.SMART_CLOTHING.get(), ProtectionLevel.NONE);
		ClothingRepresentation.register(ModItems.CASUAL_GREEN_CLOTHING.get(), ProtectionLevel.NONE);
		ClothingRepresentation.register(ModItems.BUILDER_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.BUSINESS_CLOTHING.get(), ProtectionLevel.NONE);
		ClothingRepresentation.register(ModItems.SEC_GUARD_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.MIL_HAZMAT_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.FULL_GHILLIE_CLOTHING.get(), ProtectionLevel.MEDIUM);
		ClothingRepresentation.register(ModItems.RED_DUSK_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.CLONE_CLOTHING.get(), ProtectionLevel.NONE);
		ClothingRepresentation.register(ModItems.COOKIE_CLOTHING.get(), ProtectionLevel.NONE);
		ClothingRepresentation.register(ModItems.DEADPOOL_CLOTHING.get(), ProtectionLevel.NONE);
		ClothingRepresentation.register(ModItems.NINJA_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.ARMY_MEDIC_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.BLUE_DUSK_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.PRESIDENT_CLOTHING.get(), ProtectionLevel.NONE);
		ClothingRepresentation.register(ModItems.YELLOW_DUSK_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.ORANGE_DUSK_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.GREEN_DUSK_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.WHITE_DUSK_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.PURPLE_DUSK_CLOTHING.get(), ProtectionLevel.LOW);
		ClothingRepresentation.register(ModItems.SCUBA_CLOTHING.get(), ProtectionLevel.MEDIUM);
		ClothingRepresentation.register(ModItems.DDPAT_CLOTHING.get(), ProtectionLevel.HIGH);
		ClothingRepresentation.register(ModItems.CONTRACTOR_CLOTHING.get(), ProtectionLevel.NONE);
	}

	@SubscribeEvent
	public void handleGunFire(GunEvent.TriggerPressed event) {
		//Fazer cast direto de PlayerEntity player = event.getLiving().getEntity() da ClassCastException
		getServerDist().ifPresent(server -> {
			Entity entity = event.getLiving().getEntity();
			entity.getCapability(ModCapabilities.LIVING).ifPresent(living -> {
				if(living.getEntity() instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity) living.getEntity();
					ItemStack stack = event.getItemStack();
					stack.getCapability(ModCapabilities.GUN).ifPresent(gunController -> {
						if (gunController.getPaint().isPresent()) {
							PaintItem paint = (PaintItem) gunController.getPaintStack().getItem();
							String permissionNode = "armacraft.skins."
									+ stack.getItem().getRegistryName().getPath() + "."
									+ paint.getRegistryName().getPath();
							if (ServerDist.PERMISSION_BRIDGE != null && !ServerDist.PERMISSION_BRIDGE.hasPermission(player.getUUID(), permissionNode)) {
								entity.sendMessage(new TranslationTextComponent("message.no_skin_permission")
										.setStyle(Style.EMPTY.applyFormat(TextFormatting.RED).withBold(true)), Util.NIL_UUID);
								stack.getCapability(ModCapabilities.GUN).ifPresent(x -> x.setPaintStack(ItemStack.EMPTY));
							}
						}
					});
				}
			});
		});
	}

	@SubscribeEvent
	public void onPlayerTick(LivingUpdateEvent event) {
		if (event.getEntityLiving().level.isClientSide()) {
			return;
		}

		// Remove o efeito de absorp regen se acabou os coras amarelos
		if (event.getEntityLiving().hasEffect(ArmaCraftEffects.ABSORPTION_REGENERATION.get())
				&& event.getEntityLiving().getAbsorptionAmount() <= 0) {
			event.getEntityLiving().removeEffect(ArmaCraftEffects.ABSORPTION_REGENERATION.get());

			// Som pra avisar o player
			MiscUtil.playSoundAtEntity(event.getEntityLiving(), SoundEvents.ELDER_GUARDIAN_CURSE, 1F, 1.75F);
			MiscUtil.playSoundAtEntity(event.getEntityLiving(), SoundEvents.GLASS_BREAK, 2F, 0.8F);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onGunHit(GunEvent.HitEntity event) {
		if (event.isHeadshot()) {

			CommonGunDataWrapper data = CustomGunDataController.INSTANCE.getCommonGunData(event.getItemStack().getItem().getRegistryName().toString()).get();

			// Matematicamente remove o multiplier de headshot do dano e aplica o nosso no
			// lugar
			event.setDamage((event.getDamage() / AbstractGun.HEADSHOT_MULTIPLIER) * data.getHeadshotMultiplier());
		}
	}

	// CLOTHING HANDLER
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void handleClothing(LivingHurtEvent event) {
		// Só no caso
		if (event.isCanceled()) {
			return;
		}

		event.getEntityLiving().getCapability(ModCapabilities.LIVING).ifPresent(living -> {

			// Proteção primária gratuita, sem equipamentos necessários
			event.setAmount(event.getAmount() * (1F - ProtectionLevel.FREE_PROTECTION_MODIFIER));

			InventorySlotType[] enchantableSlots = { InventorySlotType.CLOTHING, InventorySlotType.HAT,
					InventorySlotType.VEST };

			int totalEnchantmentLevel = 0;

			ItemStack clothingStack = living.getItemHandler().getStackInSlot(InventorySlotType.CLOTHING.getIndex());
			float clothingProtection = ClothingRepresentation.from(clothingStack)
					.map(clothing -> clothing.getProtectionLevel().getProtection()).orElse(1F);

			for (InventorySlotType slot : enchantableSlots) {
				ItemStack stack = living.getItemHandler().getStackInSlot(slot.getIndex());
				totalEnchantmentLevel += EnchantUtils.getEnchantmentLevel(stack, Enchantments.PROJECTILE_PROTECTION);
			}

			// Cuidado pra não enfiar um zero, senão ninguém morre
			event.setAmount(
					event.getAmount() * (1F - (ProtectionLevel.PROTECTION_ENCHANTMENT_MODIFIER * totalEnchantmentLevel))
							* (clothingProtection));
		});
	}
	
	public ArmaDist getDist() {
		return this.dist;
	}

	public Optional<ServerDist> getServerDist() {
		if (this.dist instanceof ServerDist) {
			return Optional.of((ServerDist) this.dist);
		}

		return Optional.empty();
	}

	public Optional<ClientDist> getClientDist() {
		if (this.dist instanceof ClientDist) {
			return Optional.ofNullable((ClientDist) this.dist);
		}

		return Optional.empty();
	}

	public static ArmaCraft getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Instance not available yet");
		}

		return instance;
	}
}
