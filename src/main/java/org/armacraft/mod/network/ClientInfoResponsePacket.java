package org.armacraft.mod.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;
import org.armacraft.mod.util.GameFolder;
import org.armacraft.mod.util.MiscUtil;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientInfoResponsePacket {

	private final Map<GameFolder, FolderSnapshotDTO> clientFolders;
	private final List<String> clientTransformationServices;

	private ClientInfoResponsePacket(Map<GameFolder, FolderSnapshotDTO> snapshots,
			List<String> transformationServices) {
		this.clientFolders = snapshots;
		this.clientTransformationServices = transformationServices;
	}

	public static void encode(ClientInfoResponsePacket msg, PacketBuffer out) {
		msg.clientFolders.entrySet().forEach(entry -> {
			out.writeUtf(entry.getKey().getFolderName());
			entry.getValue().write(out);
		});

		out.writeByte(msg.clientTransformationServices.size());
		msg.clientTransformationServices.forEach(out::writeUtf);
	}

	public static ClientInfoResponsePacket decode(PacketBuffer in) {
		// @StringObfuscator:on
		ClientInfoResponsePacket modsPacket = ClientInfoResponsePacket.empty();

		for (int i = 0; i < GameFolder.values().length; i++) {
			String folderName = in.readUtf(50);
			FolderSnapshotDTO folderSnapshot = FolderSnapshotDTO.fromInput(in);

			
			GameFolder folder = GameFolder.fromFolderName(folderName)
					.orElseThrow(() -> new RuntimeException("Game folder " + folderName + " was not expected"));

			if (modsPacket.clientFolders.containsKey(folder)) {
				throw new RuntimeException("Already received data about "+folder.getFolderName());
			}
			
			modsPacket.clientFolders.put(folder, folderSnapshot);
		}

		byte transformationServices = in.readByte();

		if (transformationServices > 10) {
			throw new RuntimeException("Too many transformation services: " + transformationServices);
		}

		for (int i = 0; i < transformationServices; i++) {
			modsPacket.clientTransformationServices.add(in.readUtf(50));
		}
		
		// @StringObfuscator:off

		return modsPacket;
	}

	public static boolean handle(ClientInfoResponsePacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (!ctx.get().getDirection().getReceptionSide().isServer()) {
			return true;
		}

		ctx.get().enqueueWork(() -> {
			for (GameFolder gameFolder : GameFolder.values()) {
				// Nesse ponto, essa variavel deve NUNCA ser null
				FolderSnapshotDTO clientFolderSnapshot = msg.clientFolders.get(gameFolder);
				ArmaCraft.getInstance().getDist().validateUntrustedFolder(clientFolderSnapshot, ctx.get().getSender());
			}

			// Recebe os transformation services que nao sao exatamente mods (Optifine,
			// etc...)
			ArmaCraft.getInstance().getDist().validateTransformationServices(msg.clientTransformationServices, ctx.get().getSender());
		});

		return true;
	}

	public static ClientInfoResponsePacket empty() {
		return new ClientInfoResponsePacket(new HashMap<>(), new ArrayList<>());
	}

	public static ClientInfoResponsePacket withMyMods() {
		return new ClientInfoResponsePacket(GameFolder.createSnapshotsForAll(), MiscUtil.getTransformationServices());
	}
}
