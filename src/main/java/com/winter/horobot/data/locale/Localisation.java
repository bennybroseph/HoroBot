package com.winter.horobot.data.locale;

import com.winter.horobot.data.cache.HoroCache;
import com.winter.horobot.exceptions.UpdateFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ResourceBundle;

public class Localisation {

	/**
	 * All of the languages available, some with UTF8Control for out of range characters
	 */
	private static ResourceBundle enLang = ResourceBundle.getBundle("locale.en");

	private static final Logger LOGGER = LoggerFactory.getLogger(Localisation.class);

	/**
	 * Check a guilds language
	 * @param guild The guild to check the language of
	 * @return Returns the 2 letter code for the language
	 */
	private static String checkLanguage(IGuild guild) {
		return HoroCache.get(guild).getLanguage();
	}

	/**
	 * Change a guilds language to something else
	 * @param guild The guild to change
	 * @param language The language to change to
	 * @return true on success, false on failure
	 */
	public static boolean changeLanguage(IGuild guild, String language) throws UpdateFailedException {
		switch (language) {
			case "en":
				updateGuildLanguage(guild, "en");
				return true;
			case "nl":
				updateGuildLanguage(guild, "nl");
				return true;
			case "es":
				updateGuildLanguage(guild, "es");
				return true;
			case "pt":
				updateGuildLanguage(guild, "pt");
				return true;
			case "hi":
				updateGuildLanguage(guild, "hi");
				return true;
			case "fr":
				updateGuildLanguage(guild, "fr");
				return true;
			case "de":
				updateGuildLanguage(guild, "de");
				return true;
			case "ru":
				updateGuildLanguage(guild, "ru");
				return true;
			case "ro":
				updateGuildLanguage(guild, "ro");
				return true;
			default:
				return false;
		}
	}

	/**
	 * Update a guilds language
	 * @param guild The guild to update
	 * @param language The language to update to
	 */
	private static void updateGuildLanguage(IGuild guild, String language) throws UpdateFailedException {
		HoroCache.get(guild).setLanguage(language);
	}

	/**
	 * Get a localised message
	 * @param guild The guild to get the message for
	 * @param messageKey The key of the message to get
	 * @param params The params to replace in the message
	 * @return Returns a localised message
	 */
	public static String getMessage(IGuild guild, String messageKey, Object... params) {
		String lang = checkLanguage(guild);
		switch (lang) {
			case "en":
				if (enLang.containsKey(messageKey))
					return String.format(enLang.getString(messageKey), params);
				break;
			default:
				break;
		}
		LOGGER.error("Missing message for key '" + messageKey + "' in translation '" + lang + "'");
		return "Missing message for key '" + messageKey + "' in translation '" + lang + "'. This should be reported to the respective translator in the guild, (s)he should have a role with the language (s)he translates to. Simply shoot them a message, or just message Fox, whichever you fancy; https://discord.gg/MCUTSZz";
	}

	/**
	 * Shortcut for getMessage
	 * @param guild The guild to grab the message for
	 * @param messageKey The key of the message to get
	 * @param params The params to replace in the message
	 * @return localised message
	 */
	public static String of(IGuild guild, String messageKey, Object... params) {
		return getMessage(guild, messageKey, params);
	}

	/**
	 * If it's not a guild you wish to get the message for, get the default English localisation for the message
	 * @param messageKey The key of the message you wish to get
	 * @return Returns the localised message
	 */
	public static String getDefaultMessage(String messageKey) {
		if(enLang.containsKey(messageKey))
			return enLang.getString(messageKey);
		return null;
	}
}