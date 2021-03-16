package org.armacraft.mod.module.features;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;
import org.armacraft.mod.module.Configurable;
import org.armacraft.mod.module.IModule;
import org.armacraft.mod.module.ModuleSide;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.UUID;

public class ACPermissionsModule implements IModule, IPermissionHandler, Configurable {

    private Multimap<UUID, String> permissions = HashMultimap.create();

    @Override
    public void load() {
        File permissionsConfig = new File("/armacraft-modules/armacraft-permissions.json");
        if(!permissionsConfig.exists())
        PermissionAPI.setPermissionHandler(this);
    }

    @Override
    public String getId() {
        return "armacraft-permissions";
    }

    @Override
    public String getPath() {
        return "/armacraft-modules/armacraft-permissions.json";
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
