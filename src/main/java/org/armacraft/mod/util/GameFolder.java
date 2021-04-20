package org.armacraft.mod.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.armacraft.mod.network.dto.FolderSnapshotDTO;

public enum GameFolder {
	COREMODS("coremods", ".*"),
	MODS("mods", ".*"),
	BIN("bin", "(zip|jar)$");
	
	private final String folderName;
	private final String regex;
	
	private GameFolder(String folderName, String regex) {
		this.folderName = folderName;
		this.regex = regex;
	}

	public String getFolderName() {
		return folderName;
	}

	public String getRegex() {
		return regex;
	}

	public FolderSnapshotDTO createSnapshot() {
		return FolderSnapshotDTO.of(this.getFolderName(), this.getRegex());
	}
	
	public static Map<GameFolder, FolderSnapshotDTO> createSnapshotsForAll() {
		Map<GameFolder, FolderSnapshotDTO> snapshots = new HashMap<>();
		
		for (GameFolder gameFolder : GameFolder.values()) {
			snapshots.put(gameFolder, gameFolder.createSnapshot());
		}
		
		return snapshots;
	}
	
	public static Optional<GameFolder> fromFolderName(String folderName) {
		for (GameFolder folder : GameFolder.values()) {
			if (folder.getFolderName().equals(folderName)) {
				return Optional.of(folder);
			}
		}
		return Optional.empty();
	}
}
