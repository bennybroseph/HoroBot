package com.winter.horobot.profile;

import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Utility;
import com.winter.horobot.animals.wolf.WolfTemplate;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class ProfileBuilder {

	public static final int width = 300;
	public static final int height = 300;

	public static EmbedObject generateEmbed(IUser user) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.withAuthorIcon(Utility.getAvatar(user));
		builder.withAuthorName(user.getName() + "'s Profile");
		builder.withColor(Color.CYAN);
		builder.withImage("attachment://" + "profile.png");
		return builder.build();
	}

	public static byte[] generateLevelUp(IUser user, int level) {
		BufferedImage bufferedImage = new BufferedImage(100, 125, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Image avatar = null;
		try {
			BufferedImage temp = Utility.imageFor(Utility.getAvatar(user).replace(".webp", ".png"));
			avatar = temp.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ProfileTemplate template = DataBase.queryUser(user);

		// Draw the background
		graphics.drawImage(template.getBackground(), 0, 0, 100, 125, null);

		// Draw the template
		graphics.drawImage(template.getLvlTemplate(), 0, 0, 100, 125, null);

		// Draw the user avatar
		graphics.setClip(new Ellipse2D.Double(26, 17, 50, 50));
		graphics.drawImage(avatar, 26, 17, 50, 50, null);
		graphics.setClip(null);

		// Draw the level
		graphics.setFont(new Font("Roboto Medium", Font.PLAIN, 18));
		graphics.setColor(new Color(186,187,189));
		final String levelUp = "LVL " + template.getLevel();
		final int levelX = (100 / 2) - ((int) graphics.getFontMetrics().getStringBounds(levelUp, graphics).getWidth() / 2);
		final int levelY = 125 - 15;
		graphics.drawString(levelUp,
				levelX,
				levelY);

		/* LEGACY */
		/*graphics.setColor(new Color(255, 255, 255, 175));
		graphics.fillRect(0, 70, 90, 110);

		graphics.drawImage(avatar, 15, 5, 60, 60, null);

		graphics.setColor(Color.DARK_GRAY);
		graphics.setFont(font);
		graphics.drawString(
				"LEVEL UP!",
				(90 / 2) - ((int) graphics.getFontMetrics().getStringBounds("LEVEL UP!", graphics).getWidth() / 2),
				85);
		graphics.setFont(font2);
		graphics.drawString(
				"LVL " + template.getLevel(),
				(90 / 2) - ((int) graphics.getFontMetrics().getStringBounds("LVL " + template.getLevel(), graphics).getWidth() / 2),
				105);*/

		graphics.dispose();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferedImage, "png", bytes);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return bytes.toByteArray();
	}

	public static byte[] generateProfileImage(IUser user) {
		final ProfileTemplate template = DataBase.queryUser(user);

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final Font font = new Font("Roboto", Font.TRUETYPE_FONT, 16);
		graphics.setFont(font);

		Image avatar = null;
		try {
			BufferedImage temp = Utility.imageFor(Utility.getAvatar(user).replace(".webp", ".png"));
			avatar = temp.getScaledInstance(75, 75, Image.SCALE_SMOOTH);
		} catch(Exception e) {
			e.printStackTrace();
		}

		/* LEGACY */
		//final int avatarX = 20;
		//final int avatarY = ((height / 3) - (avatar.getHeight(null) / 2) - 30);

		// Draw background
		graphics.drawImage(template.getBackground(), 0, 0, width, height, null);

		// Draw the profile template
		graphics.drawImage(template.getTemplate(), 0, 0, width, height, null);

		// Draw the user avatar
		graphics.setClip(new Ellipse2D.Double(33, 30, 75, 75));
		graphics.drawImage(avatar, 33, 30, 75, 75, null);
		graphics.setClip(null);

		// Draw the username
		graphics.setFont(new Font("Roboto Black", Font.BOLD, 20));
		graphics.setColor(new Color(186,187,189));
		graphics.drawString(
				template.getName(),
				(width / 2 + width / 7) - ((int) graphics.getFontMetrics().getStringBounds(template.getName(), graphics).getWidth() / 2),
				50);

		// Fill progress bar
		int barX = 122;
		int barY = 62;
		int barHeight = 18;
		final int size = (width / 2 - 4);
		final double percent = ((double) template.getXp() / (double) template.getMaxXp()) * (double) size;
		final int barWidth = (int) percent;
		graphics.setColor(new Color(96,115,145));
		graphics.setClip(new RoundRectangle2D.Float(barX, barY, barWidth, barHeight, 10, 10));
		graphics.fillRect(barX, barY, barWidth, barHeight);
		graphics.setClip(null);

		// Draw the experience
		final int experienceX = (barX + (size / 2)) - (int) graphics.getFontMetrics().getStringBounds(template.getXp() + " / " + template.getMaxXp(), graphics).getWidth() / 3;
		final int experienceY = barY + barHeight - 4;
		graphics.setFont(new Font("Roboto Bold", Font.BOLD, 14));
		graphics.setColor(new Color(216,216,216));
		graphics.drawString(template.getXp() + " / " + template.getMaxXp(), experienceX, experienceY);

		// Draw the level
		final int levelX = (barX + (size / 2)) - (int) graphics.getFontMetrics().getStringBounds("Level " + template.getLevel(), graphics).getWidth() / 2;
		final int levelY = barY + barHeight + 20;
		graphics.setFont(new Font("Roboto Medium", Font.PLAIN, 18));
		graphics.setColor(new Color(186,187,189));
		graphics.drawString("Level " + template.getLevel(), levelX, levelY);

		// Draw the coins
		final int textX = 63;
		final int textY = (height / 2) + 15;
		graphics.setFont(new Font("Roboto Light", Font.PLAIN, 20));
		graphics.drawString("$" + template.getFoxCoins(), textX, textY + 2);

		// Draw the rank
		graphics.drawString("#" + DataBase.queryRank(user), textX, textY + 48);

		// Draw the experience
		final int experience = (template.getLevel() * 300) + (template.getLevel() * 60);
		graphics.drawString("" + experience, textX, textY + 97);

		// Draw the info
		final int infoX = (width / 2) - 13;
		final int infoY = (height / 2) + 10;
		graphics.setFont(new Font("Roboto Light", Font.PLAIN, 14));
		// Split every 18 characters
		final int count = 24;
		String[] info = template.getDescription().split("(?<=\\G.{" + count + "})");
		int y = infoY - graphics.getFontMetrics().getHeight();
		for (String line : info) {
			graphics.drawString(
					line,
					infoX,
					y += graphics.getFontMetrics().getHeight());
		}

		// Draw the wolf image
		final int wolfX = (width / 2) + 50;
		final int wolfY = (height / 2) + 50;
		graphics.drawImage(template.getWolf(), wolfX, wolfY, 128, 128, null);

		// Draw the wolf hunger
		DataBase.insertWolf(user);
		final WolfTemplate wolf = DataBase.wolfQuery(user);
		final int hungerX = wolfX - 60;
		final int hungerY = wolfY + 40;
		graphics.setFont(new Font("Roboto Light", Font.PLAIN, 13));
		graphics.drawString(wolf.getHunger() + "/" + wolf.getMaxHunger() + " Hunger", hungerX, hungerY);

		// Draw the wolf level
		final int wolfLevelY = wolfY + 80;
		graphics.setFont(new Font("Roboto Regular", Font.PLAIN, 18));
		graphics.drawString("Level " + wolf.getLevel(), hungerX, wolfLevelY);

		/* Legacy Profile */
		/*// Draw the stats background
		graphics.setColor(new Color(255, 255, 255, 175));
		graphics.fillRect(20, avatarY, width - 40, 240);

		// Draw the status border
		graphics.setStroke(new BasicStroke(3));
		graphics.setColor(new Color(128, 128, 128, 255));
		graphics.drawRect(avatarX, avatarY, width - 40, 240);
		graphics.drawLine(
				avatarX + avatar.getWidth(null),
				avatarY + avatar.getHeight(null),
				width - 20,
				avatarY + avatar.getHeight(null));

		// Draw the info box
		// Draw the line
		graphics.drawLine(
				avatarX,
				avatarY + avatar.getHeight(null) + 97,
				width - 20,
				avatarY + avatar.getHeight(null) + 97);

		// Draw the info
		graphics.setFont(new Font("Roboto", Font.BOLD, 16));
		graphics.setColor(Color.DARK_GRAY);
		String info = WordUtils.wrap(template.getDescription(), 32);
		int y = avatarY + avatar.getHeight(null) + 116 - graphics.getFontMetrics().getHeight();
		for (String line : info.split("\n")) {
			graphics.drawString(
					line,
					avatarX + 10,
					y += graphics.getFontMetrics().getHeight());
		}

		// Draw profile picture
		graphics.drawImage(avatar, avatarX, avatarY, avatar.getWidth(null), avatar.getHeight(null), null);

		// Draw profile picture border
		graphics.setStroke(new BasicStroke(3));
		graphics.setColor(new Color(128, 128, 128, 255));
		graphics.drawRect(avatarX, avatarY, avatar.getWidth(null), avatar.getHeight(null));

		// Draw name
		final int nameY = (avatarY + avatar.getHeight(null) + 30);
		graphics.setColor(Color.DARK_GRAY);
		graphics.setFont(new Font("Roboto", Font.BOLD, 26));
		graphics.drawString(
				user.getName(),
				(width / 2) - ((int) graphics.getFontMetrics().getStringBounds(user.getName(), graphics).getWidth() / 2),
				nameY);

		// Draw stats
		// Draw level
		graphics.setFont(new Font("Roboto", Font.TRUETYPE_FONT, 22));
		graphics.drawString(
				"Level " + template.getLevel(),
				(width / 2) - ((int) graphics.getFontMetrics().getStringBounds("Level " + template.getLevel(), graphics).getWidth() / 2),
				(nameY + 23));

		// Draw experience progress bar
		// Draw the actual progress
		final int size = (width / 2 + 20);
		final double percent = ((double) template.getXp() / (double) template.getMaxXp()) * (double) size;
		final int progress = (int) percent;
		graphics.setColor(Color.ORANGE);
		graphics.fillRect(
				(width / 2) - (size / 2),
				(nameY + 33),
				progress,
				(23));

		// Draw the border
		graphics.setColor(Color.DARK_GRAY);
		graphics.drawRect(
				(width / 2) - (size / 2),
				(nameY + 33),
				size,
				(23));

		// Draw the experience and max
		graphics.setFont(new Font("Roboto", Font.TRUETYPE_FONT, 18));
		graphics.drawString(
				template.getXp() + " / " + template.getMaxXp(),
				(width / 2) - ((int) graphics.getFontMetrics().getStringBounds(template.getXp() + " / " + template.getMaxXp(), graphics).getWidth() / 2),
				(nameY + 52));

		// Draw stats next to profile image
		graphics.setFont(new Font("Roboto", Font.TRUETYPE_FONT, 20));
		graphics.drawString("Coins: $" + template.getFoxCoins(),
				avatarX + avatar.getWidth(null) + 10,
				avatarY + 25);

		graphics.drawString(
				"Global Rank: #" + DataBase.queryRank(user),
				avatarX + avatar.getWidth(null) + 10,
				avatarY + 47);

		final int totalXP = (template.getLevel() * 300) + (template.getLevel() * 30);
		graphics.drawString(
				"Total XP: " + totalXP,
				avatarX + avatar.getWidth(null) + 10,
				avatarY + 69);*/

		graphics.dispose();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferedImage, "png", bytes);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return bytes.toByteArray();
	}
}