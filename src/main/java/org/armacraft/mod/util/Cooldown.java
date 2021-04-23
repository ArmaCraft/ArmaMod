package org.armacraft.mod.util;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import net.minecraft.entity.player.PlayerEntity;

public class Cooldown {

	private static final Object2LongMap<String> timerMap = Object2LongMaps.emptyMap();

	private Cooldown() {
	}
	
	public static boolean checkAndPut(PlayerEntity entity, String key, long millis) {
		return checkAndPut(MiscUtil.getPlayerName(entity) + ":::" + key, millis);
	}

	public static boolean checkAndPut(String key, long millis) {
		long timeNow = System.currentTimeMillis();

		if (!check(key, millis, timeNow)) {
			timerMap.put(key, timeNow);
			return false;
		}
		
		return true;
	}
	
	public static boolean check(String key, long millis) {
		return check(key, millis, System.currentTimeMillis());
	}
	
	public static boolean check(String key, long millis, long timeNow) {
		long lastTimestamp = timerMap.getOrDefault(key, -1L);
		return lastTimestamp > -1 && (lastTimestamp + millis >= timeNow);
	}
}
