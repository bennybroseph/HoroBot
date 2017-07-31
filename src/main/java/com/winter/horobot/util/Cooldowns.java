package com.winter.horobot.util;

import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;

public class Cooldowns {

	private static final HashMap<String, HashMap<String, Long>> COOLDOWNS = new HashMap<>();

	/**
	 * Makes a cooldown for user and bucket
	 *
	 * @param bucket The bucket (cooldown name)
	 * @param user The user to cool down
	 *
	 * @return true if the cooldown isn't over
	 */
	public static boolean onCooldown(String bucket, IUser user) {
		HashMap<String, Long> bket = COOLDOWNS.computeIfAbsent(bucket, d -> new HashMap<>());
		return bket.getOrDefault(user.getStringID(), 0L) > System.currentTimeMillis();
	}

	public static long getRemaining(String bucket, IUser user) {
		HashMap<String, Long> bket = COOLDOWNS.computeIfAbsent(bucket, d-> new HashMap<>());
		return getCooldownEndTime(bucket, user) - System.currentTimeMillis();
	}

	public static long getCooldownEndTime(String bucket, IUser user) {
		HashMap<String, Long> bket = COOLDOWNS.computeIfAbsent(bucket, d -> new HashMap<>());
		return bket.getOrDefault(user.getStringID(), 0L);
	}

	/**
	 * Puts user onto a cooldown in bucket
	 *
	 * @param bucket The bucket
	 * @param user The user
	 */
	public static void putOnCooldown(String bucket, IUser user, long cooldown){
		HashMap<String, Long> bket = COOLDOWNS.computeIfAbsent(bucket, d -> new HashMap<>());
		bket.put(user.getStringID(), System.currentTimeMillis() + cooldown);
	}
}
