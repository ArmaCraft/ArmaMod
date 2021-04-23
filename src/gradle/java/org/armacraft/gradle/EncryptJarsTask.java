package org.armacraft.gradle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;

public class EncryptJarsTask {
	public static void main(String[] args) {
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e1) {
			throw new RuntimeException(e1);
		}

		keyGenerator.init(256);

		// Generate Key
		SecretKey key = keyGenerator.generateKey();

		// Generating IV.
		byte[] IV = new byte[16];
		SecureRandom random = new SecureRandom();
		random.nextBytes(IV);

		File buildOutputFolder = new File(".", "build/libs");

		for (File jarFile : buildOutputFolder.listFiles()) {
			Map<String, byte[]> jarFiles = new HashMap<>();
			
			if (jarFile.getName().endsWith(".jar")) {
				
				// TODO Continuar
				// Falta checar se são classes antes de criptografar
				// Falta criar e usar certificados (um pra cada distribuição)
				// E testar no client a descriptografia
				
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
					if (shouldEncryptFile(fileName)) {
						System.out.println("Encrypting: " + fileName);
						byte[] input = entry.getValue();
						
						for (int i = 0; i < input.length; i++) {
							input[i] = (byte) ~input[i];
						}
					}
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
	
	public static boolean shouldEncryptFile(String file) {
		return file.endsWith(".class") && !file.toLowerCase().contains("mixin");
	}

	public static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] IV) {
		try {
			// Get Cipher Instance
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// Create SecretKeySpec
			SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

			// Create IvParameterSpec
			IvParameterSpec ivSpec = new IvParameterSpec(IV);

			// Initialize Cipher for ENCRYPT_MODE
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

			// Perform Encryption
			byte[] cipherText = cipher.doFinal(plaintext);

			return cipherText;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] decrypt(byte[] cipherText, SecretKey key, byte[] IV) {
		try {
			// Get Cipher Instance
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// Create SecretKeySpec
			SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

			// Create IvParameterSpec
			IvParameterSpec ivSpec = new IvParameterSpec(IV);

			// Initialize Cipher for DECRYPT_MODE
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

			// Perform Decryption
			return cipher.doFinal(cipherText);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
