package org.armacraft.mod.bridge;

import com.craftingdead.core.item.gun.AbstractGun;
import com.craftingdead.core.item.gun.AbstractGunType;

public interface AbstractGunBridge<T extends AbstractGunType<SELF>, SELF extends AbstractGun<T, SELF>> {
	T bridge$getGunType();
}
