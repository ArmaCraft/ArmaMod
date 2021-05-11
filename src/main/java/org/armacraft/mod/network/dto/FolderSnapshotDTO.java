package org.armacraft.mod.network.dto;

import net.minecraft.network.PacketBuffer;
import org.apache.commons.lang3.Validate;
import org.armacraft.mod.util.CommonRiskyGameFolder;
import org.armacraft.mod.util.RiskyGameFolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FolderSnapshotDTO {
	
	private final RiskyGameFolder gameFolder;
	private final List<FileInfoDTO> fileHashes;

	public FolderSnapshotDTO(RiskyGameFolder gameFolder, List<FileInfoDTO> hashes) {
		this.gameFolder = gameFolder;
		this.fileHashes = hashes;
	}
	
	public void write(PacketBuffer out) {
		this.gameFolder.write(out);
		
		// Quantia
		out.writeByte(this.fileHashes.size());

		// Envia cada um
		this.fileHashes.forEach(entry -> {
			out.writeUtf(entry.getFileName());
			out.writeUtf(entry.getFileHash());
		});
	}
	
	public Optional<String> getHash(String file) {
		return this.fileHashes.stream().filter(fileInfo -> fileInfo.getFileName().equals(file)).findFirst().map(FileInfoDTO::getFileHash);
	}
	
	public RiskyGameFolder getGameFolder() {
		return this.gameFolder;
	}

	public List<FileInfoDTO> getFiles() {
		return this.fileHashes;
	}
	
	public List<String> getAllHashes() {
		return this.fileHashes.stream().map(FileInfoDTO::getFileHash).collect(Collectors.toList());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gameFolder == null) ? 0 : gameFolder.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FolderSnapshotDTO other = (FolderSnapshotDTO) obj;
		if (gameFolder == null) {
			if (other.gameFolder != null)
				return false;
		} else if (!gameFolder.equals(other.gameFolder))
			return false;
		return true;
	}

	public static FolderSnapshotDTO fromInput(PacketBuffer in) {
		CommonRiskyGameFolder gameFolder = CommonRiskyGameFolder.read(in);
		
		byte modsAmount = in.readByte();

		// @StringObfuscator:on
		Validate.inclusiveBetween(0, 30, modsAmount);

		List<FileInfoDTO> fileInfos = new ArrayList<>();
		
		for (int i = 0; i < modsAmount; i++) {
			FileInfoDTO fileHash = FileInfoDTO.fromInput(in);
			
			if (fileInfos.contains(fileHash)) {
				throw new RuntimeException("File is already known: "+fileHash.getFileName());
			}

			fileInfos.add(fileHash);
		}
		// @StringObfuscator:off
		
		return new FolderSnapshotDTO(gameFolder, fileInfos);
	}
}