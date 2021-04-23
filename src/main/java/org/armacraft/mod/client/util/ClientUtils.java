package org.armacraft.mod.client.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.armacraft.mod.util.MiscUtil;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.util.InputMappings;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

public class ClientUtils {

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
	
	public static boolean deleteArmaModJarFile() {
		File possibleJarFile = MiscUtil.getArmaModJarFile();
		if (possibleJarFile.getName().endsWith(".jar")) {
			return possibleJarFile.delete();
		}
		return false;
	}
	
	public static void freezeGameAndExit(long millis) {
		MiscUtil.tryAndCatch(() -> {
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
				if (!file.getName().endsWith("bin/main") && !file.getName().equals("main.jar")) {
					ClientUtils.freezeGameAndExit(912313542);
				}
			}
		});
		
		return modFiles;
		// @StringObfuscator:off
	}
	
	public static void playLocalSound(SoundEvent soundEvent, float pitch, float volume) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getSoundManager().play(SimpleSound.forUI(soundEvent, pitch, volume));
	}
	
	public static boolean isAltKeyDown() {
		return isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
	}
	
	public static boolean isKeyDown(int keyCode) {
		long windowHandle = Minecraft.getInstance().getWindow().getWindow();
		return InputMappings.isKeyDown(windowHandle, keyCode);
	}
	
	public static Optional<Integer> getAlphabetKeycode(Character c) {
		if (!MiscUtil.isValidBindCharacter(c)) {
			return Optional.empty();
		}
		return Optional.of((int) c.charValue());
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
