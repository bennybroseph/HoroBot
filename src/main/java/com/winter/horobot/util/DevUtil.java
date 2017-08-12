package com.winter.horobot.util;

import com.winter.horobot.Main;
import com.winter.horobot.exceptions.ErrorHandler;
import sx.blah.discord.util.Image;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class DevUtil {

	public static boolean changeAvatar(String uri) {
		try {
			URL url = new URL(uri);
			Main.getClient().changeAvatar(Image.forUrl("png", url.toURI().toASCIIString()));
			return true;
		} catch (MalformedURLException | URISyntaxException e) {
			ErrorHandler.log(e, "Tried changing avatar to " + uri);
		}
		return false;
	}

}