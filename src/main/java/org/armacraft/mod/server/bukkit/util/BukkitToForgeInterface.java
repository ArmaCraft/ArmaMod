package org.armacraft.mod.server.bukkit.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.bridge.bukkit.IUserData;
import org.armacraft.mod.network.ClientEnvironmentRequestPacket;
import org.armacraft.mod.network.ClientInfoRequestPacket;
import org.armacraft.mod.network.CloseGamePacket;
import org.armacraft.mod.network.CommonGunSpecsUpdatePacket;
import org.armacraft.mod.network.MACAddressRequestPacket;
import org.armacraft.mod.network.UpdateUserDataPacket;
import org.armacraft.mod.server.CustomGunDataController;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

@OnlyIn(Dist.DEDICATED_SERVER)
public enum BukkitToForgeInterface {
	INSTANCE;
	
	private Method craftPlayer$getHandle;

	public void synchronizeUserData(IUserData data) {
		ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().stream()
				.filter(player -> player.getUUID().equals(data.getHolder()))
				.forEach(player ->
					ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> player),
							new UpdateUserDataPacket(data))
				);
	}

	public void requestMACAdress(Player player) {
		ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> this.getPlayerEntity(player)),
				new MACAddressRequestPacket());
	}

	public void packAndSynchronizeGuns(Player player) {
		CustomGunDataController.INSTANCE.resendGunData(getPlayerEntity(player));
	}
	
	public void closePlayerGame(Player player, String title, String message) {
		player.kickPlayer(message);
		ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> this.getPlayerEntity(player)),
				new CloseGamePacket(title, message));
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
