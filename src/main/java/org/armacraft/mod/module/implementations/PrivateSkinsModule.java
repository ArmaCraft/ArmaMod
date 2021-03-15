package org.armacraft.mod.module.implementations;

import com.craftingdead.core.item.GunItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.armacraft.mod.module.IModule;
import org.armacraft.mod.module.ModuleState;

import java.util.concurrent.atomic.AtomicReference;

public class PrivateSkinsModule implements IModule {
    @Override
    public void load() {

        final DeferredRegister<Item> CD_ITEMS = com.craftingdead.core.item.ModItems.ITEMS;
        CD_ITEMS.getEntries().stream()
                .filter(registry -> registry.get() instanceof GunItem)
                .map(RegistryObject::get).forEach(gun -> {
                    AtomicReference<String> rootNode = new AtomicReference<>("armacraft.skins." + gun.getRegistryName().toString());
                    ((GunItem)gun).getAcceptedPaints().forEach(paint -> {
                        String permissionNode = rootNode.get() + "." + paint.getRegistryName().toString();
                        PermissionAPI.registerNode(permissionNode, DefaultPermissionLevel.ALL, "");
                    });
                });
    }

    @Override
    public String getId() {
        return "private-skins";
    }
}
