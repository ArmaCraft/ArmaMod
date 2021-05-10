package org.armacraft.mod.server.bukkit.util;

import java.lang.reflect.Method;

import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.item.ModItems;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.bridge.IAbstractGunTypeBridge;
import org.armacraft.mod.bridge.bukkit.IUserData;
import org.armacraft.mod.network.ClientEnvironmentRequestPacket;
import org.armacraft.mod.network.ClientInfoRequestPacket;
import org.armacraft.mod.network.CloseGamePacket;
import org.armacraft.mod.network.CommonGunSpecsUpdatePacket;
import org.armacraft.mod.network.UpdateUserDataPacket;
import org.armacraft.mod.util.GunUtils;
import org.armacraft.mod.util.RegistryUtil;
import org.armacraft.mod.wrapper.CommonGunInfoWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

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

	public void synchronizeGuns(CommonGunInfoWrapper infos) {
		RegistryUtil.filterRegistries(GunItem.class, ModItems.ITEMS).stream()
				.filter(registry -> registry.getId().toString().equalsIgnoreCase(infos.getResourceLocation()))
				.map(gun -> (GunItem) gun.get())
				.forEach(gun -> ((IAbstractGunTypeBridge) gun.getGunType()).bridge$updateSpecs(infos));
		GunUtils.getCommonGunSpecsWrapper(infos.getResourceLocation()).ifPresent(x -> {
			Bukkit.getServer().getOnlinePlayers().forEach(player ->
				ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> this.getPlayerEntity(player)),
						new CommonGunSpecsUpdatePacket(x))
			);
		});
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
