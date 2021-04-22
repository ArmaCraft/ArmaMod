package org.armacraft.mod.util;

import net.minecraft.network.PacketBuffer;

public interface RiskyGameFolder {
	String getFolderPath();
	String getFinderRegex();
	
	default boolean isSameFolder(RiskyGameFolder folder) {
		return folder.getFolderPath().equals(folder.getFolderPath());
	}
	
	void write(PacketBuffer out);
}
