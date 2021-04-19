package org.armacraft.mod.network.dto;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import net.minecraft.network.PacketBuffer;

public class FolderSnapshotDTO {
	
	private static final Logger LOGGER = LogManager.getLogger();
	private String folderName;
	private Map<String, String> fileHashes = new HashMap<>();
	
	public FolderSnapshotDTO() {}

	public FolderSnapshotDTO(String folderName, Map<String, String> hashes) {
		this.folderName = folderName;
		this.fileHashes = hashes;
	}
	
	public void write(PacketBuffer out) {
		out.writeByte(this.fileHashes.size());

		this.fileHashes.entrySet().forEach(entry -> {
			out.writeUtf(entry.getKey());
			out.writeUtf(entry.getValue());
		});
	}
	
	public Optional<String> getHash(String file) {
		return Optional.ofNullable(this.fileHashes.get(file));
	}
	
	public String getFolderName() {
		return this.folderName;
	}

	public Map<String, String> getFileHashesMap() {
		return this.fileHashes;
	}
	
	public List<String> getFileHashes() {
		return ImmutableList.copyOf(this.fileHashes.values());
	}
	
	public static FolderSnapshotDTO fromInput(PacketBuffer in) {
		FolderSnapshotDTO newSnapshot = new FolderSnapshotDTO();
		
		byte modsAmount = in.readByte();

		if (modsAmount > 30) {
			throw new RuntimeException("Too many mods: " + modsAmount);
		}

		for (int i = 0; i < modsAmount; i++) {
			String modId = in.readUtf(50);
			String hash = in.readUtf(32);

			newSnapshot.fileHashes.put(modId, hash);
		}
		
		return newSnapshot;
	}

	public static FolderSnapshotDTO of(String path, String fileRegex) {
		return of(new File(path), fileRegex);
	}
	
	public static FolderSnapshotDTO of(File folder, String fileRegex) {
		Map<String, String> hashes = new HashMap<>();
		
		if (folder.exists()) {
			for (File file : folder.listFiles()) {
				// Not a folder (for example, when running in the dev workspace)
				if (file.isFile() && file.getName().matches(fileRegex)) {
					LOGGER.info("File {} matches {}", file.getName(), fileRegex);
					String hash = getHash(file);
					hashes.put(file.getName(), hash);
				}
			}
		}
		
		return new FolderSnapshotDTO(folder.getName(), hashes);
	}
	
	public static String getHash(File file) {
		try {
			return Files.hash(file, Hashing.md5()).toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}