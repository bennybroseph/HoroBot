package com.winter.horobot.data.schedule;

import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;

public class Cooldowns {

	private static final HashMap<String, HashMap<String, Long>> COOLDOWNS = new HashMap<>();

	/**
	 * Check if a user's bucket is still on cool-down
	 * @param bucket The unique bucket
	 * @param user The user to get the bucket for
	 * @return True if still on cool-down, false if not
	 */
	public static boolean onCooldown(String bucket, IUser user) {
		HashMap<String, Long> bket = COOLDOWNS.computeIfAbsent(bucket, d -> new HashMap<>());
		return bket.getOrDefault(user.getStringID(), 0L) > System.currentTimeMillis();
	}

	/**
	 * Get the remaining time left on a user's bucket
	 * @param bucket The unique bucket
	 * @param user The user to get the bucket for
	 * @return The remaining time for a cool-down
	 */
	public static long getRemaining(String bucket, IUser user) {
		return getCooldownEndTime(bucket, user) - System.currentTimeMillis();
	}

	/**
	 * Get the end time for a cool-down
	 * @param bucket The unique bucket
	 * @param user The user to get the bucket for
	 * @return The end time in system time
	 */
	private static long getCooldownEndTime(String bucket, IUser user) {
		HashMap<String, Long> bket = COOLDOWNS.computeIfAbsent(bucket, d -> new HashMap<>());
		return bket.getOrDefault(user.getStringID(), 0L);
	}

	/**
	 * Put a bucket on cool-down for someone
	 * @param bucket The unique bucket
	 * @param user The user to get the bucket for
	 * @param cooldown The cool-down length
	 */
	public static void putOnCooldown(String bucket, IUser user, long cooldown){
		HashMap<String, Long> bket = COOLDOWNS.computeIfAbsent(bucket, d -> new HashMap<>());
		bket.put(user.getStringID(), System.currentTimeMillis() + cooldown);
	}
}