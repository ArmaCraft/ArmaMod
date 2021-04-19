package org.armacraft.mod.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;
import org.armacraft.mod.server.GameFolder;
import org.armacraft.mod.util.MiscUtil;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientInfoResponsePacket {

	private final Map<GameFolder, FolderSnapshotDTO> clientSnapshots;
	private final List<String> clientTransformationServices;

	private ClientInfoResponsePacket(Map<GameFolder, FolderSnapshotDTO> snapshots,
			List<String> transformationServices) {
		this.clientSnapshots = snapshots;
		this.clientTransformationServices = transformationServices;
	}

	public static void encode(ClientInfoResponsePacket msg, PacketBuffer out) {
		out.writeByte(msg.clientSnapshots.size());

		msg.clientSnapshots.entrySet().forEach(entry -> {
			out.writeUtf(entry.getKey().getFolderName());
			entry.getValue().write(out);
		});

		out.writeByte(msg.clientTransformationServices.size());
		msg.clientTransformationServices.forEach(out::writeUtf);
	}

	public static ClientInfoResponsePacket decode(PacketBuffer in) {
		ClientInfoResponsePacket modsPacket = ClientInfoResponsePacket.empty();

		byte snapshotsEntries = in.readByte();

		for (int i = 0; i < snapshotsEntries; i++) {
			String folderName = in.readUtf(50);
			FolderSnapshotDTO folderSnapshot = FolderSnapshotDTO.fromInput(in);

			GameFolder folder = GameFolder.fromFolderName(folderName)
					.orElseThrow(() -> new RuntimeException("Game folder " + folderName + " was not expected"));

			if (modsPacket.clientSnapshots.containsKey(folder)) {
				throw new RuntimeException("Already received data about "+folder.getFolderName());
			}
			
			modsPacket.clientSnapshots.put(folder, folderSnapshot);
		}

		byte transformationServices = in.readByte();

		if (transformationServices > 10) {
			throw new RuntimeException("Too many transformation services: " + transformationServices);
		}

		for (int i = 0; i < transformationServices; i++) {
			modsPacket.clientTransformationServices.add(in.readUtf(50));
		}

		return modsPacket;
	}

	public static boolean handle(ClientInfoResponsePacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (!ctx.get().getDirection().getReceptionSide().isServer()) {
			return true;
		}

		ctx.get().enqueueWork(() -> {
			final String playerName = MiscUtil.getPlayerName(ctx.get().getSender());

			for (GameFolder serverGameFolder : GameFolder.values()) {
				FolderSnapshotDTO clientFolderSnapshot = msg.clientSnapshots.get(serverGameFolder);
				ArmaCraft.getInstance().getDist().validateUntrustedFolder(clientFolderSnapshot, ctx.get().getSender());
			}

			// Recebe os transformation services que nao sao exatamente mods (Optifine,
			// etc...)
			for (String transformationService : msg.clientTransformationServices) {
				MiscUtil.runConsoleCommand("delegategameinfo tfservice " + playerName + " " + transformationService);
			}
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
