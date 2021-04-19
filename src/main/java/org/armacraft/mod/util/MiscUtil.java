package org.armacraft.mod.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.craftingdead.core.util.ModDamageSource;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class MiscUtil {

	/**
	 * Optifine eh um exemplo de tranformation service. Ele nao aparece na lista de
	 * mods do Forge.
	 */
	public static List<String> getTransformationServices() {
		final List<Map<String, String>> modList = Launcher.INSTANCE.environment()
				.getProperty(IEnvironment.Keys.MODLIST.get()).orElseThrow(() -> new RuntimeException("Not set"));

		return modList.stream().filter(map -> {
			if (!"TRANSFORMATIONSERVICE".equals(map.get("type"))) {
				return false;
			}

			return true;
		}).map(map -> map.get("name")).collect(Collectors.toList());
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
	
	public static void runWithoutHeadlessMode(Runnable runnable) {
		String valueBefore = System.getProperty("java.awt.headless");
        System.setProperty("java.awt.headless", "false");
        runnable.run();
        System.setProperty("java.awt.headless", valueBefore);
	}
	
	public static boolean isUsingJava11() {
		return System.getProperty("java.version").startsWith("11");
	}
	
	public static void silentyCatchException(Runnable runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			// shhh....
		}
	}
}
