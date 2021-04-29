package org.armacraft.mod.bridge;

import com.craftingdead.core.item.GunItem;
import org.armacraft.mod.wrapper.CommonGunInfoWrapper;

public interface IGunItemBridge {
    void bridge$updateSpecs(CommonGunInfoWrapper infos);
}
