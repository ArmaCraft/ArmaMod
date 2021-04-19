package org.armacraft.mod.bridge.bukkit;

import java.util.Set;
import java.util.UUID;

public interface IUserData {
    UUID getHolder();
    Set<String> getNametagWhitelist();
    Set<String> getFlags();
}
