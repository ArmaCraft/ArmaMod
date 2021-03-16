package org.armacraft.mod;

import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public interface PermissionChecker {
    boolean checkPermission(UUID player, String permission);
}
