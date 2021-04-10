package org.armacraft.mod;

import java.util.concurrent.atomic.AtomicReference;

import org.armacraft.mod.clothing.ClothingRepresentation;
import org.armacraft.mod.clothing.ProtectionLevel;
import org.armacraft.mod.init.ArmaCraftBlocks;
import org.armacraft.mod.init.ArmaCraftItems;
import org.armacraft.mod.init.ArmaCraftTileEntityTypes;
import org.armacraft.mod.init.ArmaDist;
import org.armacraft.mod.init.ClientDist;
import org.armacraft.mod.init.ServerDist;
import org.armacraft.mod.potion.ArmaCraftEffects;
import org.armacraft.mod.util.EnchantUtils;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.capability.gun.GunImpl;
import com.craftingdead.core.event.GunEvent;
import com.craftingdead.core.inventory.InventorySlotType;
import com.craftingdead.core.item.ModItems;
import com.craftingdead.core.util.ModDamageSource;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(ArmaCraft.MODID)
public class ArmaCraft {

	public static final String MODID = "armacraft";
	private static final String NETWORK_VERSION = "1.0.0";
	private static ArmaCraft instance;

	public static float ARMACRAFT_HEADSHOT_MULTIPLIER = 1.5F;
	public static PermissionChecker PERMISSION_CHECKER;
	public static IEventBus modEventBus;
	
	public static final SimpleChannel networkChannel = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(ArmaCraft.MODID, "play")).clientAcceptedVersions(NETWORK_VERSION::equals)
			.serverAcceptedVersions(NETWORK_VERSION::equals).networkProtocolVersion(() -> NETWORK_VERSION)
			.simpleChannel();

	private ArmaDist dist;

	public ArmaCraft() {
		instance = this;
		
		modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.register(this);
		modEventBus.addListener(this::handleCommonSetup);

		ArmaCraftBlocks.BLOCKS.register(modEventBus);
		ArmaCraftItems.ITEMS.register(modEventBus);
		ArmaCraftEffects.EFFECTS.register(modEventBus);
		ArmaCraftTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

		this.dist = DistExecutor.safeRunForDist(() -> ClientDist::new, () -> ServerDist::new);
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

	/**
	 * Listener pro {@link MinecraftForge}.EVENT_BUS
	 */
	@Mod.EventBusSubscriber
	public static class ClothingHandlers {

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
			AtomicReference<Float> sourceDamage = new AtomicReference<>(event.getAmount());
			event.getEntityLiving().getCapability(ModCapabilities.LIVING).ifPresent(living -> {
				ItemStack clothingStack = living.getItemHandler().getStackInSlot(InventorySlotType.CLOTHING.getIndex());
				if (!living.getItemHandler().getStackInSlot(InventorySlotType.CLOTHING.getIndex()).isEmpty()) {
					ClothingRepresentation.from(clothingStack).ifPresent(clothing -> {
						if (EnchantUtils.hasEnchant(clothingStack, Enchantments.PROJECTILE_PROTECTION)) {
							int level = EnchantUtils.getEnchantNBT(clothingStack, Enchantments.PROJECTILE_PROTECTION)
									.getInt("lvl");
							sourceDamage.set(sourceDamage.get() * (clothing.getProtectionLevel().getProtection()
									* ProtectionLevel.PROTECTION_BASE_MULTIPLIER
									- level * ProtectionLevel.PROTECTION_MULTIPLIER));
						} else {
							sourceDamage.set(sourceDamage.get() - clothing.getProtectionLevel().getProtection());
						}
					});
				}

				ItemStack vestStack = living.getItemHandler().getStackInSlot(InventorySlotType.VEST.getIndex());
				if (!living.getItemHandler().getStackInSlot(InventorySlotType.CLOTHING.getIndex()).isEmpty()) {
					if (EnchantUtils.hasEnchant(vestStack, Enchantments.PROJECTILE_PROTECTION)) {
						int level = EnchantUtils.getEnchantNBT(vestStack, Enchantments.PROJECTILE_PROTECTION)
								.getInt("lvl");
						sourceDamage.set(sourceDamage.get() * 0.86f * ProtectionLevel.PROTECTION_BASE_MULTIPLIER
								- level * ProtectionLevel.PROTECTION_MULTIPLIER);
					} else {
						sourceDamage.set(sourceDamage.get() - 0.86f);
					}
				}

				ItemStack bootStack = living.getItemHandler().getStackInSlot(EquipmentSlotType.FEET.getIndex());
				if (!living.getItemHandler().getStackInSlot(InventorySlotType.CLOTHING.getIndex()).isEmpty()) {
					float baseDefense = 1f;
					float rawDefense = 1f;
					if (bootStack.getItem().equals(Items.NETHERITE_BOOTS)) {
						baseDefense = 0.75f;
						rawDefense = 2;
					} else if (bootStack.getItem().equals(Items.DIAMOND_BOOTS)) {
						baseDefense = 0.8f;
						rawDefense = 1.5f;
					} else if (bootStack.getItem().equals(Items.IRON_BOOTS)) {
						baseDefense = 0.85f;
					}
					if (EnchantUtils.hasEnchant(bootStack, Enchantments.PROJECTILE_PROTECTION)) {
						int level = EnchantUtils.getEnchantNBT(bootStack, Enchantments.PROJECTILE_PROTECTION)
								.getInt("lvl");
						sourceDamage.set(sourceDamage.get() * baseDefense * ProtectionLevel.PROTECTION_BASE_MULTIPLIER
								- level * ProtectionLevel.PROTECTION_MULTIPLIER);
					} else {
						sourceDamage.set(sourceDamage.get() - rawDefense);
					}
				}

				if (event.getSource().getMsgId().equalsIgnoreCase(ModDamageSource.BULLET_HEADSHOT_DAMAGE_TYPE)) {
					ItemStack helmetStack = living.getItemHandler().getStackInSlot(EquipmentSlotType.FEET.getIndex());
					if (!living.getItemHandler().getStackInSlot(InventorySlotType.CLOTHING.getIndex()).isEmpty()) {
						if (EnchantUtils.hasEnchant(bootStack, Enchantments.PROJECTILE_PROTECTION)) {
							int level = EnchantUtils.getEnchantNBT(helmetStack, Enchantments.PROJECTILE_PROTECTION)
									.getInt("lvl");
							sourceDamage.set(sourceDamage.get() * 0.86f * ProtectionLevel.PROTECTION_BASE_MULTIPLIER
									- level * ProtectionLevel.PROTECTION_MULTIPLIER);
						} else {
							sourceDamage.set(sourceDamage.get() - 0.86f);
						}
					}
				}
			});

			event.setAmount(sourceDamage.get());
		}
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
