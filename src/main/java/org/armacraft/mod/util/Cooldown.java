package org.armacraft.mod.util;

import java.util.HashMap;
import java.util.Map;

public class Cooldown {

	private static final Map<String, Long> timerMap = new HashMap<>();

	private Cooldown() {
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
		Long lastTimestamp = timerMap.get(key);
		return (lastTimestamp != null && lastTimestamp.longValue() + millis >= timeNow);
	}
}
