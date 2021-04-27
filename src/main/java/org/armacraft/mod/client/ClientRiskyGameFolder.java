package org.armacraft.mod.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.armacraft.mod.network.dto.FileInfoDTO;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;
import org.armacraft.mod.util.CommonRiskyGameFolder;
import org.armacraft.mod.util.FileUtil;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT)
public class ClientRiskyGameFolder extends CommonRiskyGameFolder {

	private final File folderFile;
	
	public ClientRiskyGameFolder(String folderPath, String regex) {
		super(folderPath, regex);
		this.folderFile = new File(folderPath);
	}
	
	public File getFolder() {
		return this.folderFile;
	}
	
	public static List<ClientRiskyGameFolder> allClientRiskyFolders() {
		// @StringObfuscator:on
		
		/*
		 * USAREI ISTO DEPOIS
		 * 
		 * List<ClientRiskyGameFolder> foundRiskyFolders =
		 * ClientUtils.getAllFoldersWithMods().stream().map(folder -> { return new
		 * ClientRiskyGameFolder(folder.getAbsolutePath(), ".*");
		 * }).collect(Collectors.toList());
		 * 
		 * // Adiciona os defaults (bin, mods...)
		 * foundRiskyFolders.addAll(CommonRiskyGameFolder.defaults().stream().filter(
		 * commonRiskyFolder -> { // Não existe na lista de pastas arriscadas return
		 * foundRiskyFolders.stream().noneMatch(riskyFolder ->
		 * riskyFolder.isSameFolder(commonRiskyFolder));
		 * }).map(CommonRiskyGameFolder::asClient).collect(Collectors.toList()));
		 * 
		 */

		List<ClientRiskyGameFolder> riskyFolders = CommonRiskyGameFolder.defaults().stream()
				.map(CommonRiskyGameFolder::asClient).collect(Collectors.toList());

		return ImmutableList.copyOf(riskyFolders);
		// @StringObfuscator:off
	}
	
	public static List<FolderSnapshotDTO> createSnapshotsOfAllRiskyFolders() {
		return allClientRiskyFolders().stream().map(folder -> folder.createSnapshot()).collect(Collectors.toList());
	}
	
	public FolderSnapshotDTO createSnapshot() {
		List<FileInfoDTO> hashes = new ArrayList<>();
		
		for (File file : this.getFolder().listFiles()) {
			// Not a folder (for example, when running in the dev workspace)
			if (file.isFile() && file.getName().matches(this.getFinderRegex())) {
				String hash = FileUtil.getHash(file);
				hashes.add(new FileInfoDTO(file.getName(), hash));
			}
		}
		
		return new FolderSnapshotDTO(this, hashes);
	}

}
