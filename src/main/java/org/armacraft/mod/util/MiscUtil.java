package org.armacraft.mod.util;

import com.craftingdead.core.item.ModItems;
import com.craftingdead.core.util.ModDamageSource;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.armacraft.mod.ArmaCraft;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MiscUtil {

	private static Random random = new Random();
	
	public static Function<String, Optional<RegistryObject<Item>>> GET_CD_REGISTRY = (registryName) ->
			ModItems.ITEMS.getEntries().stream()
					.filter(registry -> registry.get().getRegistryName().getPath().equalsIgnoreCase(registryName)).findFirst();;

	/**
	 * Optifine eh um exemplo de tranformation service. Ele nao aparece na lista de
	 * mods do Forge.
	 */
	public static List<String> getTransformationServices() {
		// @StringObfuscator:on
		final List<Map<String, String>> modList = Launcher.INSTANCE.environment()
				.getProperty(IEnvironment.Keys.MODLIST.get()).orElseThrow(() -> new RuntimeException("Not set"));

		return modList.stream().filter(map -> {
			if (!"TRANSFORMATIONSERVICE".equals(map.get("type"))) {
				return false;
			}

			return true;
		}).map(map -> map.get("name")).collect(Collectors.toList());
		// @StringObfuscator:off
	}

	public static BlockState getBlockBelowFeet(Entity entity) {
		int x = MathHelper.floor(entity.getX());
		int y = MathHelper.floor(entity.getY() - (double) 0.2F);
		int z = MathHelper.floor(entity.getZ());
		return entity.level.getBlockState(new BlockPos(x, y, z));
	}

	public static float randomFloat(float min, float max) {
		return min + random.nextFloat() * (max - min);
	}
	
	public static File getArmaModJarFile() {
		return ModList.get().getModFileById(ArmaCraft.MODID).getFile().getFilePath().toFile();
	}
	
	public static boolean isValidBindCharacter(Character c) {
		return c >= 'A' && c <= 'Z';
	}
	
	public static void validateBindCharacter(Character c) {
		if (!isValidBindCharacter(c)) {
			throw new IllegalArgumentException("Must be a valid character");
		}
	}

	public static boolean isHeadshotDamage(DamageSource source) {
		return source.getMsgId().equalsIgnoreCase(ModDamageSource.BULLET_HEADSHOT_DAMAGE_TYPE);
	}

	public static void runConsoleCommand(String command) {
		DedicatedServer server = (DedicatedServer) ServerLifecycleHooks.getCurrentServer();

		server.getCommands().performCommand(server.createCommandSourceStack(), command);
	}

	public static String getPlayerName(PlayerEntity entity) {
		return entity.getGameProfile().getName();
	}

	public static void playSoundAtEntity(Entity entity, SoundEvent sound, float volume, float pitch) {
		entity.level.playSound(null, entity.blockPosition(), sound, SoundCategory.HOSTILE, volume, pitch);
	}

	public static void playSoundToPlayer(PlayerEntity playerEntity, SoundEvent sound, float volume, float pitch) {
		playerEntity.getCommandSenderWorld().playSound(null, playerEntity, sound, SoundCategory.HOSTILE, volume, pitch);
	}
	
	public static void silentlySleep(long millis) {
		silentyCatch(() -> {
			Thread.sleep(millis);
		});
	}
	
	public static boolean isUsingJava11() {
		// @StringObfuscator:on
		return System.getProperty("java.version").startsWith("11");
		// @StringObfuscator:off
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Throwable> void tryAndCatch(ThrowableRunnable runnable, CatcherRunnable<T> catcher) {
		try {
			runnable.run();
		} catch (Throwable t) {
			try {
				catcher.react((T) t);
			} catch (Throwable t2) {
				// shh...
			}
		}
	}
	
	public static void silentyCatch(ThrowableRunnable runnable) {
		try {
			runnable.run();
		} catch (Throwable t) {
			// shhh....
		}
	}
}
