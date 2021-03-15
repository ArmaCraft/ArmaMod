package org.armacraft.mod.module.implementations;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.Hash;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;
import org.armacraft.mod.module.IModule;
import org.armacraft.mod.module.ModuleSide;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class ACPermissionsModule implements IModule, IPermissionHandler {

    private Multimap<UUID, String> permissions = HashMultimap.create();

    @Override
    public void load() {
        PermissionAPI.setPermissionHandler(this);
    }

    @Override
    public String getId() {
        return "ac-permissions";
    }

    @Override
    public ModuleSide getSide() {
        return ModuleSide.SERVER;
    }

    @Override
    public void registerNode(String node, DefaultPermissionLevel level, String desc) {

    }

    @Override
    public Collection<String> getRegisteredNodes() {
        return null;
    }

    @Override
    public boolean hasPermission(GameProfile profile, String node, @Nullable IContext context) {
        return false;
    }

    @Override
    public String getNodeDescription(String node) {
        return null;
    }
}
