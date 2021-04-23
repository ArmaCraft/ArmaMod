package org.armacraft.mod.util;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;

public class Cooldown {

	private static final Object2LongMap<String> timerMap = new Object2LongOpenHashMap<>();

	private Cooldown() {
	}
	
	public static boolean checkAndPut(PlayerEntity entity, String key, long millis) {
		return checkAndPut(MiscUtil.getPlayerName(entity) + ":::" + key, millis);
	}

	public static boolean checkAndPut(String key, long millis) {
		long timeNow = System.currentTimeMillis();

		if (!check(key, millis, timeNow)) {
			System.out.println(timerMap);
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
