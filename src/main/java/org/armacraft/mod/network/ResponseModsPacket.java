package org.armacraft.mod.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.util.ModsUtil;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

public class ResponseModsPacket {

	private static final Logger LOGGER = LogManager.getLogger();
	private Map<String, String> hashes;
	
	private ResponseModsPacket(Map<String, String> hashes) {
		this.hashes = hashes;
	}
	
	public static void encode(ResponseModsPacket msg, PacketBuffer out) {
		out.writeByte(msg.hashes.size());

		msg.hashes.entrySet().forEach(entry -> {
			out.writeUtf(entry.getKey());
			out.writeUtf(entry.getValue());
		});
	}

	public static ResponseModsPacket decode(PacketBuffer in) {
		ResponseModsPacket modsPacket = ResponseModsPacket.empty();

		byte amount = in.readByte();

		if (amount > 50) {
			throw new RuntimeException("Too many mods: "+amount);
		}

		for (int i = 0; i < amount; i++) {
			String modId = in.readUtf(100);
			String hash = in.readUtf(32);

			modsPacket.hashes.put(modId, hash);
		}

		return modsPacket;
	}

	public static boolean handle(ResponseModsPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isServer()) {
			ctx.get().enqueueWork(() -> {
				Map<String, String> serverHashes = ArmaCraft.getInstance().getServerDist().getHashes();
				for (Entry<String, String> entry : msg.hashes.entrySet()) {
					String clientModId = entry.getKey();
					String clientModHash = entry.getValue();

					String theModHashInServer = serverHashes.get(clientModId);

					// Mod existe no server
					if (theModHashInServer != null) {
						// Hash n�o bate
						if (!clientModHash.equals(theModHashInServer)) {
							ctx.get().getNetworkManager().disconnect(
									new StringTextComponent("Connection closed - mod files are different from server"));
							return;
						}
					} else {
						LOGGER.info("Client has a mod that does not exists in server files: " + clientModId);
					}
				}
			});
		}
		return true;
	}
	
	public static ResponseModsPacket empty() {
		return new ResponseModsPacket(new HashMap<>());
	}
	
	public static ResponseModsPacket withMyMods() {
		return new ResponseModsPacket(ModsUtil.calculateMyHashes());
	}
}
