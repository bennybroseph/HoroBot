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

	private static final Map<IGuild, GuildMeta> GUILD_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
	private static final Map<IUser, UserMeta> USER_CACHE = Collections.synchronizedMap(new WeakHashMap<>());

	/**
	 * Get a guild from the cache, if the guild is not already in the cache the guild will be put into it
	 *
	 * @param guild The guild implementation to get the meta for
	 * @return The guild meta for the guild implementation
	 */
	public static GuildMeta get(IGuild guild) {
		synchronized (GUILD_CACHE) {
			if (!GUILD_CACHE.containsKey(guild)) {
				LOGGER.debug("Trying to put user " + guild.getName() + " " + guild.getStringID() + " in the guild cache...");
				Database.set("INSERT INTO guilds.guild (id, language, prefixes, autoroles, welcome) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING;", guild.getStringID(), "en", new String[]{}, new Long[]{}, "none");
				GUILD_CACHE.put(guild, new GuildMeta(guild));
				LOGGER.debug("Put guild " + guild.getName() + " " + guild.getStringID() + " in the guild cache");
			}
			return GUILD_CACHE.get(guild);
		}
	}

	/**
	 * Get a user from the cache, if the user is not already in the cache the user will be put into it
	 *
	 * @param user The user implementation to get the meta for
	 * @return The user meta for the user implementation
	 */
	public static UserMeta get(IUser user) {
		synchronized (USER_CACHE) {
			if (!USER_CACHE.containsKey(user)) {
				LOGGER.debug("Trying to put user " + user.getName() + " " + user.getStringID() + " in the user cache...");
				Database.set("INSERT INTO users.user (id) VALUES (?) ON CONFLICT DO NOTHING;", user.getStringID());
				USER_CACHE.put(user, new UserMeta(user));
				LOGGER.debug("Put guild " + user.getName() + " " + user.getStringID() + " in the user cache");
			}
			return USER_CACHE.get(user);
		}
	}

	/**
	 * Get the guild cache
	 *
	 * @return The guild cache
	 */
	public static Map<IGuild, GuildMeta> getGuildCache() {
		synchronized (GUILD_CACHE) {
			return GUILD_CACHE;
		}
	}

	/**
	 * Get the user cache
	 *
	 * @return The user cache
	 */
	public static Map<IUser, UserMeta> getUserCache() {
		synchronized (USER_CACHE) {
			return USER_CACHE;
		}
	}
}