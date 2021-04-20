package org.armacraft.mod.network.dto;

import net.minecraft.network.PacketBuffer;

public class FileInfoDTO {

	private final String fileName;
	private final String fileHash;

	public FileInfoDTO(String fileName, String fileHash) {
		this.fileName = fileName;
		this.fileHash = fileHash;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getFileHash() {
		return this.fileHash;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileHash == null) ? 0 : fileHash.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
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
		FileInfoDTO other = (FileInfoDTO) obj;
		if (fileHash == null) {
			if (other.fileHash != null)
				return false;
		} else if (!fileHash.equals(other.fileHash))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

	public static FileInfoDTO fromInput(PacketBuffer in) {
		String fileName = in.readUtf(50);
		String hash = in.readUtf(32);
		
		return new FileInfoDTO(fileName, hash);
	}
}
