package org.armacraft.mod.bridge.bukkit;

import com.google.common.collect.Multimap;

import java.util.Map;
import java.util.UUID;

public interface IBukkitNametagControllerBridge {
    Multimap<UUID, String> getNametagVisibility();
    Map<UUID, Boolean> getNametagUpdateWatcher();
    void setNametagUpdate(UUID to, Boolean value);
}
