package org.armacraft.mod.bridge.bukkit;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IBukkitUserDataControllerBridge {
    Set<IUserData> getUsersData();
    Optional<IUserData> getUserData(UUID uuid);
    Map<UUID, Boolean> getUserDataUpdateWatcher();
    void updateWatcher(UUID uuid, Boolean bool);
}
