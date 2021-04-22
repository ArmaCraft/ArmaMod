package org.armacraft.mod.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.client.ClientRiskyGameFolder;
import org.armacraft.mod.network.dto.FolderSnapshotDTO;
import org.armacraft.mod.util.MiscUtil;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientInfoResponsePacket {

	private final List<FolderSnapshotDTO> clientFolders;
	private final List<String> clientTransformationServices;

	private ClientInfoResponsePacket(List<FolderSnapshotDTO> snapshots,
			List<String> transformationServices) {
		this.clientFolders = snapshots;
		this.clientTransformationServices = transformationServices;
	}

	public static void encode(ClientInfoResponsePacket msg, PacketBuffer out) {
		// Quantidade de pastas
		out.writeByte(msg.clientFolders.size());
		
		// As pastas em si
		msg.clientFolders.forEach(folderSnapshot -> {
			folderSnapshot.write(out);
		});

		out.writeByte(msg.clientTransformationServices.size());
		msg.clientTransformationServices.forEach(out::writeUtf);
	}

	public static ClientInfoResponsePacket decode(PacketBuffer in) {
		// @StringObfuscator:on
		ClientInfoResponsePacket thePacket = ClientInfoResponsePacket.empty();

		byte amountOfClientFolders = in.readByte();
		
		Validate.inclusiveBetween(1, 15, amountOfClientFolders);
		
		for (int i = 0; i < amountOfClientFolders; i++) {
			FolderSnapshotDTO folderSnapshot = FolderSnapshotDTO.fromInput(in);

			if (thePacket.clientFolders.contains(folderSnapshot)) {
				throw new RuntimeException("Already received data about "+folderSnapshot.getGameFolder().getFolderPath());
			}
			
			thePacket.clientFolders.add(folderSnapshot);
		}

		byte transformationServices = in.readByte();

		Validate.inclusiveBetween(1, 15, transformationServices);

		for (int i = 0; i < transformationServices; i++) {
			thePacket.clientTransformationServices.add(in.readUtf(50));
		}
		
		// @StringObfuscator:off

		return thePacket;
	}

	public static boolean handle(ClientInfoResponsePacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (!ctx.get().getDirection().getReceptionSide().isServer()) {
			return true;
		}

		ctx.get().enqueueWork(() -> {
			ArmaCraft.getInstance().getDist().validateUntrustedFolders(msg.clientFolders, ctx.get().getSender());

			// Valida os transformation services que nao sao exatamente mods (Optifine,
			// etc...)
			ArmaCraft.getInstance().getDist().validateTransformationServices(msg.clientTransformationServices, ctx.get().getSender());
		});

		return true;
	}

	public static ClientInfoResponsePacket empty() {
		return new ClientInfoResponsePacket(new ArrayList<>(), new ArrayList<>());
	}

	public static ClientInfoResponsePacket withMyMods() {
		return new ClientInfoResponsePacket(ClientRiskyGameFolder.createSnapshotsOfAllRiskyFolders(), MiscUtil.getTransformationServices());
	}
}
