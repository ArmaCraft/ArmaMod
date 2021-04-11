package org.armacraft.mod.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.util.MiscUtil;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ResponseModsPacket {

	private Map<String, String> hashes;
	private List<String> transformationServices;

	private ResponseModsPacket(Map<String, String> hashes, List<String> transformationServices) {
		this.hashes = hashes;
		this.transformationServices = transformationServices;
	}

	public static void encode(ResponseModsPacket msg, PacketBuffer out) {
		out.writeByte(msg.hashes.size());

		msg.hashes.entrySet().forEach(entry -> {
			out.writeUtf(entry.getKey());
			out.writeUtf(entry.getValue());
		});

		out.writeByte(msg.transformationServices.size());

		msg.transformationServices.forEach(out::writeUtf);
	}

	public static ResponseModsPacket decode(PacketBuffer in) {
		ResponseModsPacket modsPacket = ResponseModsPacket.empty();

		byte modsAmount = in.readByte();

		if (modsAmount > 20) {
			throw new RuntimeException("Too many mods: " + modsAmount);
		}

		for (int i = 0; i < modsAmount; i++) {
			String modId = in.readUtf(50);
			String hash = in.readUtf(32);

			modsPacket.hashes.put(modId, hash);
		}

		byte transformationServices = in.readByte();

		if (transformationServices > 10) {
			throw new RuntimeException("Too many transformation services: " + transformationServices);
		}

		for (int i = 0; i < modsAmount; i++) {
			modsPacket.transformationServices.add(in.readUtf(50));
		}

		return modsPacket;
	}

	public static boolean handle(ResponseModsPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isServer()) {
			ctx.get().enqueueWork(() -> {

				final String playerName = ctx.get().getSender().getGameProfile().getName();

				Map<String, String> serverHashes = ArmaCraft.getInstance().getServerDist().getHashes();
				
				// Valida hashes
				for (Entry<String, String> entry : msg.hashes.entrySet()) {
					String clientFileName = entry.getKey();
					String clientModHash = entry.getValue();

					String theModHashInServer = serverHashes.get(clientFileName);

					// Mod existe no server
					if (theModHashInServer != null) {
						// Hash n�o bate
						if (!clientModHash.equals(theModHashInServer)) {
							MiscUtil.runConsoleCommand(
									"delegatemodinfo hashmismatch " + playerName + " " + clientFileName + " " + clientModHash);
						}
					} else {
						MiscUtil.runConsoleCommand(
								"delegatemodinfo unknownfile " + playerName + " " + clientFileName + " " + clientModHash);
					}
				}
				
				// Recebe os transformation services que nao sao exatamente mods (Optifine, etc...)
				for (String transformationService : msg.transformationServices) {
					MiscUtil.runConsoleCommand(
							"delegatemodinfo tfservice " + playerName + " " + transformationService);
				}
			});
		}
		return true;
	}

	public static ResponseModsPacket empty() {
		return new ResponseModsPacket(new HashMap<>(), new ArrayList<>());
	}

	public static ResponseModsPacket withMyMods() {
		return new ResponseModsPacket(MiscUtil.calculateMyHashes(), MiscUtil.getTransformationServices());
	}
}
