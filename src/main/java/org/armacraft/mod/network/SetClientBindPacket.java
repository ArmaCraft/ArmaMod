package org.armacraft.mod.network;

import java.util.function.Supplier;

import org.armacraft.mod.ArmaCraft;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetClientBindPacket {
	
	private Character character;
	private String command;
	
	public SetClientBindPacket(Character character, String command) {
		this.character = character;
		this.command = command;
	}
	
	public static void encode(SetClientBindPacket msg, PacketBuffer out) {
		out.writeChar(msg.character);
		out.writeUtf(msg.command);
	}

	public static SetClientBindPacket decode(PacketBuffer in) {
		Character character = in.readChar();
		String command = in.readUtf(150);
		return new SetClientBindPacket(character, command);
	}

	public static boolean handle(SetClientBindPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isClient()) {
			ctx.get().enqueueWork(() -> {
				ArmaCraft.getInstance().getClientDist().get().setBind(msg.character, msg.command);
			});
		}
		return true;
	}
}
