package org.armacraft.mod.server;

import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.bridge.IAbstractGunTypeBridge;
import org.armacraft.mod.network.CommonGunSpecsUpdatePacket;
import org.armacraft.mod.util.RegistryUtil;
import org.armacraft.mod.wrapper.CommonGunDataWrapper;
import org.armacraft.mod.wrapper.ResourceLocationWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomGunDataController {
    public static CustomGunDataController INSTANCE;

    private Map<String, CommonGunDataWrapper> cachedGunData = new HashMap<>();

    public Map<String, CommonGunDataWrapper> getCachedGunData() {
        return cachedGunData;
    }

    public Optional<CommonGunDataWrapper> getCommonGunData(GunItem gun) {
        return Optional.ofNullable(this.cachedGunData.get(gun.getRegistryName().toString()));
    }

    public Optional<CommonGunDataWrapper> getCommonGunData(ResourceLocationWrapper resourceLocation) {
        return Optional.ofNullable(this.cachedGunData.get(resourceLocation.toString()));
    }

    public Optional<CommonGunDataWrapper> getCommonGunData(String resourceLocation) {
        return Optional.ofNullable(this.cachedGunData.get(resourceLocation));
    }

    public void populateDefaultData() {
        this.cachedGunData.clear();
        RegistryUtil.filterRegistries(GunItem.class, ModItems.ITEMS).stream().map(gun -> (GunItem) gun.get())
                .forEach(gun -> {
                    ResourceLocationWrapper resourceLocationWrapper = ResourceLocationWrapper.of(gun.getRegistryName().toString());
                    this.cachedGunData.put(resourceLocationWrapper.toString(), CommonGunDataWrapper.of(gun, ArmaCraft.DEFAULT_HEADSHOT_MULTIPLIER));
                });
    }

    public void update(CommonGunDataWrapper wrapper) {
        String resourceLocation = wrapper.getResourceLocation().toString();
        this.cachedGunData.remove(resourceLocation);
        this.cachedGunData.put(resourceLocation, wrapper);
        RegistryUtil.filterRegistries(GunItem.class, ModItems.ITEMS).stream()
                .filter(registry -> registry.getId().toString().equalsIgnoreCase(resourceLocation))
                .map(gun -> (GunItem) gun.get())
                .forEach(gun -> ((IAbstractGunTypeBridge) gun.getGunType()).bridge$updateSpecs(wrapper));
    }

    public void resendGunData(PlayerEntity player) {
        cachedGunData.values().forEach(data -> {
            ArmaCraft.networkChannel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                    new CommonGunSpecsUpdatePacket(data));
        });
    }


}
