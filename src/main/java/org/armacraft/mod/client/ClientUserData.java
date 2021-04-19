package org.armacraft.mod.client;

import net.minecraft.client.Minecraft;
import org.armacraft.mod.bridge.bukkit.IUserData;

import java.util.Set;
import java.util.UUID;

public class ClientUserData {

    private Set<String> nametagWhitelist;
    private Set<String> flags;

    public static ClientUserData from(IUserData data) {
        return new ClientUserData(data.getNametagWhitelist(), data.getFlags());
    }
    public ClientUserData(Set<String> nametagWhitelist, Set<String> flags) {
        this.nametagWhitelist = nametagWhitelist;
        this.flags = flags;
    }

    public UUID getHolder() {
        return Minecraft.getInstance().player.getUUID();
    }

    public Set<String> getNametagWhitelist() {
        return nametagWhitelist;
    }

    public Set<String> getFlags() {
        return flags;
    }
}
