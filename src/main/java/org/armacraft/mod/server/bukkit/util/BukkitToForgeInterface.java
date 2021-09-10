package org.armacraft.mod.server.bukkit.util;

import com.craftingdead.core.capability.ModCapabilities;
import net.minecraft.entity.player.PlayerEntity;
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
import org.armacraft.mod.network.FlagsUpdatePacket;
import org.armacraft.mod.network.KeybindingsUpdatePacket;
import org.armacraft.mod.network.MACAddressRequestPacket;
import org.armacraft.mod.network.NametagsUpdatePacket;
import org.armacraft.mod.server.CustomGunDataController;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.DEDICATED_SERVER)
public enum BukkitToForgeInterface {
    INSTANCE;

    private Method craftPlayer$getHandle;

    public void synchronizeUserData(IUserData data, boolean flags, boolean nametags, boolean keybindings) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().stream()
                .filter(player -> player.getUUID().equals(data.getHolder()))
                .forEach(player -> {
                    if(flags) {
                        ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> player),
                                new FlagsUpdatePacket(data.getFlags()));
                    }
                    if(nametags) {
                        ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> player),
                                new NametagsUpdatePacket(data.getNametagWhitelist()));
                    }
                    if(keybindings) {
                        ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> player),
                                new KeybindingsUpdatePacket(new ArrayList<>(data.getKeyBinds())));
                    }
                });
    }

    public void setStackInSlot(Player p, ItemStack stack, int slot) {
        this.getPlayerEntity(p).getCapability(ModCapabilities.LIVING).ifPresent(living -> {
            living.getItemHandler().setStackInSlot(slot, CraftItemStack.asNMSCopy(stack));
        });
    }

    public List<ItemStack> getCDInventory(Player p) {
        List<ItemStack> items = new ArrayList<>();
        this.getPlayerEntity(p).getCapability(ModCapabilities.LIVING).ifPresent(living -> {
            items.add(CraftItemStack.asBukkitCopy(living.getItemHandler().getStackInSlot(0)));
            items.add(CraftItemStack.asBukkitCopy(living.getItemHandler().getStackInSlot(1)));
            items.add(CraftItemStack.asBukkitCopy(living.getItemHandler().getStackInSlot(2)));
            items.add(CraftItemStack.asBukkitCopy(living.getItemHandler().getStackInSlot(3)));
            items.add(CraftItemStack.asBukkitCopy(living.getItemHandler().getStackInSlot(4)));
        });
        return items;
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
