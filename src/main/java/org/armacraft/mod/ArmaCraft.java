package org.armacraft.mod;

import java.util.Set;

import org.armacraft.mod.bridge.INametagControllerBridge;
import org.armacraft.mod.client.ClientDist;
import org.armacraft.mod.clothing.ClothingRepresentation;
import org.armacraft.mod.clothing.ProtectionLevel;
import org.armacraft.mod.init.ArmaCraftBlocks;
import org.armacraft.mod.init.ArmaCraftItems;
import org.armacraft.mod.init.ArmaCraftTileEntityTypes;
import org.armacraft.mod.network.ClientDashPacket;
import org.armacraft.mod.network.ClientInfoRequestPacket;
import org.armacraft.mod.network.ClientInfoResponsePacket;
import org.armacraft.mod.network.UpdateVisibleNametagsPacket;
import org.armacraft.mod.potion.ArmaCraftEffects;
import org.armacraft.mod.server.ServerDist;
import org.armacraft.mod.util.EnchantUtils;
import org.armacraft.mod.util.MiscUtil;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.capability.gun.GunImpl;
import com.craftingdead.core.event.GunEvent;
import com.craftingdead.core.inventory.InventorySlotType;
import com.craftingdead.core.item.ModItems;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(ArmaCraft.MODID)
public class ArmaCraft {

	public static final String MODID = "armacraft";
	private static final String NETWORK_VERSION = "1.0.0";
	private static ArmaCraft instance;

	public static float ARMACRAFT_HEADSHOT_MULTIPLIER = 1.5F;
	public static PermissionChecker PERMISSION_CHECKER;
	public static INametagControllerBridge NAMETAG_CONTROLLER;

	public static Set<String> VISIBLE_NAMETAGS;

	public static final SimpleChannel networkChannel = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(ArmaCraft.MODID, "play")).clientAcceptedVersions(NETWORK_VERSION::equals)
			.serverAcceptedVersions(NETWORK_VERSION::equals).networkProtocolVersion(() -> NETWORK_VERSION)
			.simpleChannel();

	private ArmaDist dist;

	public ArmaCraft() {
		instance = this;

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::handleCommonSetup);

		MinecraftForge.EVENT_BUS.register(this);

		ArmaCraftBlocks.BLOCKS.register(modEventBus);
		ArmaCraftItems.ITEMS.register(modEventBus);
		ArmaCraftEffects.EFFECTS.register(modEventBus);
		ArmaCraftTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

		this.dist = DistExecutor.safeRunForDist(() -> ClientDist::new, () -> ServerDist::new);

		this.setupChannel();
	}

	public void setupChannel() {
		networkChannel.messageBuilder(ClientInfoRequestPacket.class, 0x00, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientInfoRequestPacket::encode).decoder(ClientInfoRequestPacket::decode)
				.consumer(ClientInfoRequestPacket::handle).add();

		networkChannel.messageBuilder(ClientInfoResponsePacket.class, 0x01, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ClientInfoResponsePacket::encode).decoder(ClientInfoResponsePacket::decode)
				.consumer(ClientInfoResponsePacket::handle).add();

		networkChannel.messageBuilder(UpdateVisibleNametagsPacket.class, 0x02, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(UpdateVisibleNametagsPacket::encode).decoder(UpdateVisibleNametagsPacket::decode)
				.consumer(UpdateVisibleNametagsPacket::handle).add();

		networkChannel.messageBuilder(ClientDashPacket.class, 0x03, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ClientDashPacket::encode).decoder(ClientDashPacket::decode)
				.consumer(ClientDashPacket::handle).add();
	}

	public void handleCommonSetup(FMLCommonSetupEvent event) {
		this.registerClothings();
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
			// Matematicamente remove o multiplier de headshot do dano e aplica o nosso no
			// lugar
			event.setDamage((event.getDamage() / GunImpl.HEADSHOT_MULTIPLIER) * ARMACRAFT_HEADSHOT_MULTIPLIER);
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

	public ServerDist getServerDist() {
		if (this.dist instanceof ServerDist) {
			return (ServerDist) this.dist;
		}

		throw new IllegalStateException("Server dist is not available for the current dist");
	}

	public ClientDist getClientDist() {
		if (this.dist instanceof ClientDist) {
			return (ClientDist) this.dist;
		}

		throw new IllegalStateException("Client dist is not available for the current dist");
	}

	public static ArmaCraft getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Instance not available yet");
		}

		return instance;
	}
}
