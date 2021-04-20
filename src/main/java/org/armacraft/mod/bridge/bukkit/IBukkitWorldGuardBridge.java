package org.armacraft.mod.bridge.bukkit;

import java.util.UUID;

public interface IBukkitWorldGuardBridge {
    enum State { NOT_PRESENT, ALLOWED, DENIED }
    State testState(String flag, UUID world, int x, int y, int z);
}
