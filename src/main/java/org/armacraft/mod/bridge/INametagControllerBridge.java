package org.armacraft.mod.bridge;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Map;
import java.util.UUID;

public interface INametagControllerBridge {
    Multimap<UUID, String> getNametagVisibility();
    Map<UUID, Boolean> getNametagUpdateWatcher();
    void setNametagUpdate(UUID to, Boolean value);
}
