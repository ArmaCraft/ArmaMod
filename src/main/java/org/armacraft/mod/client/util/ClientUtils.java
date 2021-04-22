package org.armacraft.mod.client.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.armacraft.mod.util.MiscUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

public class ClientUtils {
	
	private static Logger LOGGER = LogManager.getLogger();

	public static boolean silentlyHideFolderIfExists(File folder) {
		// @StringObfuscator:on
		try {
			Files.setAttribute(folder.toPath(), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
			return (boolean) Files.getAttribute(folder.toPath(), "dos:hidden", LinkOption.NOFOLLOW_LINKS);
		} catch (Exception e) {
			return false;
		}
		// @StringObfuscator:off
	}
	
	public static void freezeGameAndExit(long millis) {
		MiscUtil.tryAndCatch(() -> {
			LOGGER.info("Freezing game for {}", millis);
			Thread.sleep(millis);
		}, (t) -> {
			silentlyMakeGameStop();
		});
		silentlyMakeGameStop();
	}
	
	public static void silentlyMakeGameStop() {
		// @StringObfuscator:on
		
		// Tenta
		MiscUtil.silentyCatch(() -> {
			System.exit(-1);
		});
		
		// Tenta
		MiscUtil.silentyCatch(() -> {
			Minecraft.getInstance().destroy();
		});
		
		// Senão...
		MiscUtil.silentyCatch(() -> {
			Minecraft.getInstance().stop();
			// Se por acaso o jogo não fechar no próximo tick...
			// Escrevi qualquer merda no nome do erro AUHSAUHSUHASUH
			Minecraft.getInstance().delayCrash(new CrashReport("Failed at 0x00000AAAADE6FBC4", null));
			System.exit(-1);
		});
		// @StringObfuscator:off
	}
	
	public static List<ModFile> getAllClientModFiles() {
		// @StringObfuscator:on
		List<ModFile> modFiles = new ArrayList<>();
		
		FMLLoader.backgroundScanHandler.getModFiles().forEach((type, list) -> {
			modFiles.addAll(list);
		});
		
		// Encontra mods com caminho inválido - talvez mod injetado
		modFiles.forEach(modFile -> {
			File file = modFile.getFilePath().toFile();
			if (!file.exists() || !file.getName().endsWith(".jar")) {
				// @OnlyInDev:on
				// Caso for este mod
				Path thisMod;
				try {
					thisMod = Paths.get("..", "bin/main").toRealPath(LinkOption.NOFOLLOW_LINKS);
					LOGGER.info("This mod is {}", thisMod.toFile().getCanonicalPath());
					
					if (Files.isSameFile(thisMod, file.toPath().normalize())) {
						return;
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				// @OnlyInDev:off
				LOGGER.info("The game has a file that is not a jar: "+file.toPath());
				ClientUtils.freezeGameAndExit(912313542);
			}
		});
		
		return modFiles;
		// @StringObfuscator:off
	}
	
	public static List<File> getAllModsAsFiles() {
		return getAllClientModFiles().stream().map(modFile -> modFile.getFilePath().toFile()).collect(Collectors.toList());
	}
	
	public static List<File> getAllFoldersWithMods() {
		return getAllModsAsFiles().stream().map(file -> {
			File folderWithMods = file.getParentFile();
			if (!folderWithMods.exists()) {
				// A pasta-pai de um mod não existe (??)
				silentlyMakeGameStop();
			}
			
			return folderWithMods;
		}).collect(Collectors.toList());
	}
}
