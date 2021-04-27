package org.armacraft.mod.server.bukkit.util;

import java.lang.reflect.Method;

import org.apache.commons.lang.Validate;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.network.ClientEnvironmentRequestPacket;
import org.armacraft.mod.network.ClientInfoRequestPacket;
import org.armacraft.mod.network.CloseGamePacket;
import org.armacraft.mod.network.SetClientBindPacket;
import org.armacraft.mod.util.MiscUtil;
import org.bukkit.entity.Player;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

@OnlyIn(Dist.DEDICATED_SERVER)
public enum BukkitToForgeInterface {
	INSTANCE;
	
	private Method craftPlayer$getHandle;
	
	public void setBind(Player player, Character character, String command) {
		Validate.notNull(player);
		Validate.notNull(character);
		Validate.notNull(command);
		MiscUtil.validateBindCharacter(character);
		ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> this.getPlayerEntity(player)), new SetClientBindPacket(character, command));
	}
	
	public void closePlayerGame(Player player, String title, String message) {
		ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> this.getPlayerEntity(player)),
				new CloseGamePacket(title, message));
		player.kickPlayer(message);
	}

	public void requestPlayerEnvironmentInfos(Player player) {
		ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> this.getPlayerEntity(player)),
				new ClientEnvironmentRequestPacket());
	}

	public void requestClientInfos(Player player) {
		ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> this.getPlayerEntity(player)),
				new ClientInfoRequestPacket());
	}

	private ServerPlayerEntity getPlayerEntity(Player player) {
		try {
			if (this.craftPlayer$getHandle == null) {
				this.craftPlayer$getHandle = player.getClass().getDeclaredMethod("getHandle");
			}
			
			return (ServerPlayerEntity) this.craftPlayer$getHandle.invoke(player);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
