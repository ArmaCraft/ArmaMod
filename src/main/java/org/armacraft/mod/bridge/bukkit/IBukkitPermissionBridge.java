package org.armacraft.mod.bridge.bukkit;

import java.util.UUID;

public interface IBukkitPermissionBridge {
    boolean hasPermission(UUID player, String permission);
}
