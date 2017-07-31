/*
	HoroBot - An open-source Discord bot
	Copyright (C) 2017	WiNteR

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.winter.horobot.util;

import com.winter.horobot.database.DataBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Localisation {

	private static ResourceBundle enLang = ResourceBundle.getBundle("locale.en");
	private static ResourceBundle nlLang = ResourceBundle.getBundle("locale.nl");
	private static ResourceBundle esLang = ResourceBundle.getBundle("locale.es");
	private static ResourceBundle ptLang = ResourceBundle.getBundle("locale.pt");
	private static ResourceBundle hiLang = ResourceBundle.getBundle("locale.hi");
	private static ResourceBundle frLang = ResourceBundle.getBundle("locale.fr");
	private static ResourceBundle deLang = ResourceBundle.getBundle("locale.de", new UTF8Control());
	private static ResourceBundle ruLang = ResourceBundle.getBundle("locale.ru", new UTF8Control());
	private static ResourceBundle roLang = ResourceBundle.getBundle("locale.ro");

	private static String checkLanguage(String guildID) {
		return DataBase.guildQuery(guildID, "language");
	}

	public static boolean changeLanguage (String guildID, String language) {
		switch (language) {
			case "en":
				updateGuildLanguage(guildID, "en");
				return true;
			case "nl":
				updateGuildLanguage(guildID, "nl");
				return true;
			case "es":
				updateGuildLanguage(guildID, "es");
				return true;
			case "pt":
				updateGuildLanguage(guildID, "pt");
				return true;
			case "hi":
				updateGuildLanguage(guildID, "hi");
				return true;
			case "fr":
				updateGuildLanguage(guildID, "fr");
				return true;
			case "de":
				updateGuildLanguage(guildID, "de");
				return true;
			case "ru":
				updateGuildLanguage(guildID, "ru");
				return true;
			case "ro":
				updateGuildLanguage(guildID, "ro");
				return true;
		}
		return false;
	}

	private static void updateGuildLanguage (String guildID, String language) {
		DataBase.insertGuild(guildID);
		DataBase.updateGuild(guildID, "language", language);
	}
	
	public static String getMessage(String guildID, String str) {
		String lang = checkLanguage(guildID);
		switch (lang) {
			case "en":
				if (enLang.containsKey(str))
					return enLang.getString(str);
				break;
			case "nl":
				if (nlLang.containsKey(str))
					return nlLang.getString(str);
				break;
			case "es":
				if (esLang.containsKey(str))
					return esLang.getString(str);
				break;
			case "pt":
				if (ptLang.containsKey(str))
					return ptLang.getString(str);
				break;
			case "hi":
				if (hiLang.containsKey(str))
					return hiLang.getString(str);
				break;
			case "fr":
				if (frLang.containsKey(str))
					return frLang.getString(str);
				break;
			case "de":
				if (deLang.containsKey(str))
					return deLang.getString(str);
				break;
			case "ru":
				if (ruLang.containsKey(str))
					return ruLang.getString(str);
				break;
			case "ro":
				if (roLang.containsKey(str))
					return roLang.getString(str);
				break;
		}
		return "Localisation error please report this error in the Discord server so it can be fixed as quickly as possible; https://discord.gg/MCUTSZz";
	}
	
	public static String getPMMessage(String str) {
		if(enLang.containsKey(str))
			return enLang.getString(str);
		return "Localisation error please report this error in the Discord server so it can be fixed as quickly as possible; https://discord.gg/MCUTSZz";
	}
}