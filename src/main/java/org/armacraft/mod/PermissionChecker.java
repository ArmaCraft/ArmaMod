package org.armacraft.mod;

import java.util.UUID;

public interface PermissionChecker {
    boolean checkPermission(UUID player, String permission);
}
