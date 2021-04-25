package org.armacraft.gradle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;

public class EncryptJarsTask {
	public static void main(String[] args) {
		File buildOutputFolder = new File(".", "build/libs");

		for (File jarFile : buildOutputFolder.listFiles()) {
			Map<String, byte[]> jarFiles = new HashMap<>();
			
			if (jarFile.getName().contains("client")) {
				Manifest manifest = null;
				
				try (JarInputStream zis = new JarInputStream(new FileInputStream(jarFile))) {
					JarEntry zipEntry;
					while ((zipEntry = zis.getNextJarEntry()) != null) {
						jarFiles.put(zipEntry.getName(), IOUtils.toByteArray(zis));
					}
					
					manifest = zis.getManifest();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				jarFiles.entrySet().forEach(entry -> {
					String fileName = entry.getKey();
					System.out.println("Encrypting: " + fileName);
					
					entry.setValue(Base64.getEncoder().encode(entry.getValue()));
				});

				try (JarOutputStream zos = new JarOutputStream(new FileOutputStream(new File(buildOutputFolder, FilenameUtils.removeExtension(jarFile.getName()) + ".cz")), manifest)) {
					for (Entry<String, byte[]> entry : jarFiles.entrySet()) {
						zos.putNextEntry(new JarEntry(entry.getKey()));
						zos.write(entry.getValue());
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				if (!jarFile.delete()) {
					throw new RuntimeException("Tried to delete the input jar but had no success");
				}
			}
		}
	}
}
