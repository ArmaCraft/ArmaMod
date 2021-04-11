package org.armacraft.mod.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class MiscUtil {
	
	public static Map<String, String> calculateMyHashes() {
		Map<String, String> hashes = new HashMap<>();

		List<ModInfo> infos = FMLLoader.getLoadingModList().getMods();

		infos.stream().forEach(modFileInfo -> {
			File file = modFileInfo.getOwningFile().getFile().getFilePath().toFile();
			
			// Not a folder (for example, when running in the dev workspace)
			if (file.isFile()) {
				try {
					String modId = modFileInfo.getModId();
					String hash = Files.hash(file, Hashing.md5()).toString();
					
					hashes.put(modId, hash);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		return hashes;
	}
	
	public static void playSoundAtEntity(Entity entity, SoundEvent sound, float volume, float pitch) {
		entity.level.playSound(null, entity.blockPosition(), sound, SoundCategory.HOSTILE, volume, pitch);
	}
}
