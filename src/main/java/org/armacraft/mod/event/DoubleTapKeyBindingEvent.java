package org.armacraft.mod.event;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.eventbus.api.Event;

public class DoubleTapKeyBindingEvent extends Event {

	private final KeyBinding keyBinding;
	
	public DoubleTapKeyBindingEvent(KeyBinding keyBinding) {
		super();
		this.keyBinding = keyBinding;
	}
	
	public KeyBinding getKeyBinding() {
		return this.keyBinding;
	}
}
