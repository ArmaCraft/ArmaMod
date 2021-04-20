package org.armacraft.mod.network.dto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import net.minecraft.network.PacketBuffer;

public class FolderSnapshotDTO {
	
	private static final Logger LOGGER = LogManager.getLogger();
	private String folderName;
	private List<FileInfoDTO> fileHashes = new ArrayList<>();
	
	public FolderSnapshotDTO() {}

	public FolderSnapshotDTO(String folderName, List<FileInfoDTO> hashes) {
		this.folderName = folderName;
		this.fileHashes = hashes;
	}
	
	public void write(PacketBuffer out) {
		out.writeByte(this.fileHashes.size());

		this.fileHashes.forEach(entry -> {
			out.writeUtf(entry.getFileName());
			out.writeUtf(entry.getFileHash());
		});
	}
	
	public Optional<String> getHash(String file) {
		return this.fileHashes.stream().filter(fileInfo -> fileInfo.getFileName().equals(file)).findFirst().map(FileInfoDTO::getFileHash);
	}
	
	public String getFolderName() {
		return this.folderName;
	}

	public List<FileInfoDTO> getFiles() {
		return this.fileHashes;
	}
	
	public List<String> getAllHashes() {
		return this.fileHashes.stream().map(FileInfoDTO::getFileHash).collect(Collectors.toList());
	}
	
	public static FolderSnapshotDTO fromInput(PacketBuffer in) {
		FolderSnapshotDTO newSnapshot = new FolderSnapshotDTO();
		
		byte modsAmount = in.readByte();

		if (modsAmount > 30) {
			throw new RuntimeException("Too many mods: " + modsAmount);
		}

		for (int i = 0; i < modsAmount; i++) {
			FileInfoDTO fileHash = FileInfoDTO.fromInput(in);
			
			if (newSnapshot.getFiles().contains(fileHash)) {
				throw new RuntimeException("File is already known "+fileHash.getFileName());
			}

			newSnapshot.fileHashes.add(fileHash);
		}
		
		return newSnapshot;
	}

	public static FolderSnapshotDTO of(String path, String fileRegex) {
		return of(new File(path), fileRegex);
	}
	
	public static FolderSnapshotDTO of(File folder, String fileRegex) {
		List<FileInfoDTO> hashes = new ArrayList<>();
		
		if (folder.exists()) {
			for (File file : folder.listFiles()) {
				// Not a folder (for example, when running in the dev workspace)
				if (file.isFile() && file.getName().matches(fileRegex)) {
					LOGGER.info("File {} matches {}", file.getName(), fileRegex);
					String hash = getHash(file);
					hashes.add(new FileInfoDTO(file.getName(), hash));
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