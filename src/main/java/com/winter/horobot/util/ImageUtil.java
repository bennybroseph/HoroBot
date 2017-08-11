package com.winter.horobot.util;

import sx.blah.discord.handle.obj.IUser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtil {

	private static String[] defaults = {
			"6debd47ed13483642cf09e832ed0bc1b",
			"322c936a8c8be1b803cd94861bdfa868",
			"dd4dbc0016779df1378e7812eabaa04d",
			"0e291f67c9274a1abdddeb3fd919cbaa",
			"1cbd08c76f8af6dddce02c5138971129"
	};

	public static BufferedImage imageFromURL(URL url) throws IOException {
		if (url == null)
			return null;
		URLConnection con = url.openConnection();
		con.addRequestProperty("User-Agent", "Mozilla/5.0 HoroBot/2.0");
		con.connect();
		InputStream i = con.getInputStream();
		BufferedImage b = ImageIO.read(i);
		i.close();
		return b;
	}

	public static String getAvatar(IUser user) {
		return user.getAvatar() != null ? user.getAvatarURL() : getDefaultAvatar(user);
	}

	public static String getDefaultAvatar(IUser user) {
		int discrim = Integer.parseInt(user.getDiscriminator());
		return "https://discordapp.com/assets/" + defaults[discrim % defaults.length] + ".png";
	}

	public static int averageColor(BufferedImage i) {
		int[] pxs = i.getRaster().getPixels(0, 0, i.getWidth(), i.getHeight(), (int[]) null);
		int r = 0;
		int g = 0;
		int b = 0;
		for(int j = 0; j < pxs.length; j += 3) {
			r += pxs[j];
			g += pxs[j + 1];
			b += pxs[j + 2];
		}
		return new Color(r / (pxs.length / 3), g / (pxs.length / 3), b / (pxs.length / 3)).getRGB();
	}

}