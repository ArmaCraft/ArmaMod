package org.armacraft.mod.bridge.bukkit;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IBukkitUserDataControllerBridge {
    Set<IUserData> getUsersData();
    Map<UUID, Boolean> getUserDataUpdateWatcher();
    void updateWatcher(UUID uuid, Boolean bool);
}
