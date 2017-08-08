package com.winter.horobot.data.cache;

import com.winter.horobot.data.Database;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class HoroCache {

	private static Map<IGuild, GuildMeta> guildCache = Collections.synchronizedMap(new WeakHashMap<>());
	private static Map<IUser, UserMeta> userCache = Collections.synchronizedMap(new WeakHashMap<>());

	/**
	 * Get a guild from the cache, if the guild is not already in the cache the guild will be put into it
	 * @param guild The guild implementation to get the meta for
	 * @return The guild meta for the guild implementation
	 */
	public static GuildMeta get(IGuild guild) {
		if (!guildCache.containsKey(guild)) {
			Database.set("INSERT INTO guilds.guild (id, language, prefixes, autoroles, welcome) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING;", guild.getStringID(), "en", new String[]{}, new String[]{}, "none");
			guildCache.put(guild, new GuildMeta(guild));
		}
		return guildCache.get(guild);
	}

	/**
	 * Get a user from the cache, if the user is not already in the cache the user will be put into it
	 * @param user The user implementation to get the meta for
	 * @return The user meta for the user implementation
	 */
	public static UserMeta get(IUser user) {
		if (!userCache.containsKey(user)) {
			Database.set("INSERT INTO users.user (id) VALUES (?) ON CONFLICT DO NOTHING;", user.getStringID());
			userCache.put(user, new UserMeta(user));
		}
		return userCache.get(user);
	}

	/**
	 * Get the guild cache
	 * @return The guild cache
	 */
	public static Map<IGuild, GuildMeta> getGuildCache() {
		return guildCache;
	}

	/**
	 * Get the user cache
	 * @return The user cache
	 */
	public static Map<IUser, UserMeta> getUserCache() { return userCache; }
}