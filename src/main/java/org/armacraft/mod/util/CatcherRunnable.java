package org.armacraft.mod.util;

public interface CatcherRunnable<T extends Throwable> {

	void react(T throwable) throws Throwable;
}
