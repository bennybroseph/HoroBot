package com.winter.horobot.data.cache;

import com.winter.horobot.data.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class HoroCache {

	public static final Logger LOGGER = LoggerFactory.getLogger(HoroCache.class);

	private static Map<IGuild, GuildMeta> guildCache = Collections.synchronizedMap(new WeakHashMap<>());
	private static Map<IUser, UserMeta> userCache = Collections.synchronizedMap(new WeakHashMap<>());

	/**
	 * Get a guild from the cache, if the guild is not already in the cache the guild will be put into it
	 * @param guild The guild implementation to get the meta for
	 * @return The guild meta for the guild implementation
	 */
	public static GuildMeta get(IGuild guild) {
		if (!guildCache.containsKey(guild)) {
			LOGGER.debug("Trying to put guild " + guild.getName() + " " + guild.getStringID() + " in the guild cache...");
			Database.set("INSERT INTO guilds.guild (id, language, prefixes, autoroles, welcome) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING;", guild.getStringID(), "en", new String[]{}, new Long[]{}, "none");
			guildCache.put(guild, new GuildMeta(guild));
			LOGGER.debug("Put guild " + guild.getName() + " " + guild.getStringID() + " in the guild cache");
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
			LOGGER.debug("Trying to put user " + user.getName() + " " + user.getStringID() + " in the user cache...");
			Database.set("INSERT INTO users.user (id) VALUES (?) ON CONFLICT DO NOTHING;", user.getStringID());
			userCache.put(user, new UserMeta(user));
			LOGGER.debug("Put guild " + user.getName() + " " + user.getStringID() + " in the user cache");
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