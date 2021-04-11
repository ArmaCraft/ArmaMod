package org.armacraft.mod.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
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

	public static Map<String, String> calculateMyHashes() {
		Map<String, String> hashes = new HashMap<>();

		List<ModInfo> infos = FMLLoader.getLoadingModList().getMods();

		infos.stream().forEach(modInfo -> {
			File file = modInfo.getOwningFile().getFile().getFilePath().toFile();

			// Not a folder (for example, when running in the dev workspace)
			if (file.isFile()) {
				try {
					String hash = Files.hash(file, Hashing.md5()).toString();

					hashes.put(file.getName(), hash);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		return hashes;
	}

	public static void runConsoleCommand(String command) {
		DedicatedServer server = (DedicatedServer) ServerLifecycleHooks.getCurrentServer();

		server.getCommands().performCommand(server.createCommandSourceStack(), command);
	}

	public static void playSoundAtEntity(Entity entity, SoundEvent sound, float volume, float pitch) {
		entity.level.playSound(null, entity.blockPosition(), sound, SoundCategory.HOSTILE, volume, pitch);
	}
}
