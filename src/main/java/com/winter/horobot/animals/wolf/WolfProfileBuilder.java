package com.winter.horobot.animals.wolf;

import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Utility;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WolfProfileBuilder {

	private static final int width = 300;
	private static final int height = 150;

	private static final double multiplier = 1;

	public static EmbedObject generateEmbed(IUser user) {
		final WolfTemplate template = DataBase.wolfQuery(user);

		EmbedBuilder builder = new EmbedBuilder();
		builder.withAuthorIcon(Utility.getAvatar(user));
		builder.withAuthorName(user.getName() + "'s Wolf");
		builder.withColor(Color.CYAN);
		builder.appendField("Level", "" + template.getLevel(), true);
		builder.appendField("Progress", template.getHunger() + " / " + template.getMaxHunger() + " food to level " + (template.getLevel() + 1), true);
		builder.appendField("Feed Stats", "This wolf has been fed a total of " + template.getFedTimes() + " times", false);
		builder.withImage("attachment://wolf.png");
		return builder.build();
	}

	public static byte[] generateProfile(IUser user) {
		final WolfTemplate wolf = DataBase.wolfQuery(user);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw background
		graphics.drawImage(wolf.getBackground(), 0, 0, width, height, null);

		// Draw the template
		graphics.drawImage(wolf.getTemplate(), 0, 0, width, height, null);

		// Draw the wolf
		final BufferedImage wolfImage = generateAnimal(user);
		graphics.drawImage(wolfImage, 95, 0, wolfImage.getWidth() - 10, wolfImage.getHeight() - 10,  null);

		// Draw the name
		graphics.setFont(new Font("Roboto Regular", Font.PLAIN, 18));
		graphics.setColor(new Color(186,187,189));
		final int nameX = (width / 4 + 15) - (int) (graphics.getFontMetrics().getStringBounds(wolf.getName(), graphics).getWidth() / 2);
		final int nameY = 60;
		graphics.drawString(wolf.getName(), nameX, nameY);

		// Draw the level
		graphics.setFont(new Font("Roboto Medium", Font.PLAIN, 16));
		final int levelX = (width / 4 + width / 12) - (int) (graphics.getFontMetrics().getStringBounds("Level " + wolf.getLevel(), graphics).getWidth() / 2);
		final int levelY = 105;
		graphics.drawString("Level " + wolf.getLevel(), levelX, levelY);

		// Fill progress bar
		final int barX = 17;
		final int barY = 67;
		final int barHeight = 20;
		final int size = (width / 2 - 2);
		final double percent = ((double) wolf.getHunger() / (double) wolf.getMaxHunger()) * (double) size;
		//final double percent = 1 * (double) size;
		final int barWidth = (int) percent;
		graphics.setColor(new Color(150,117,96));
		graphics.setClip(new RoundRectangle2D.Float(barX, barY, barWidth, barHeight, 10, 10));
		graphics.fillRect(barX, barY, barWidth, barHeight);
		graphics.setClip(null);

		// Draw the actual progress string
		graphics.setFont(new Font("Roboto Bold", Font.BOLD, 14));
		graphics.setColor(new Color(216,216,216));
		final int textX = barX + (size / 2) - (int) (graphics.getFontMetrics().getStringBounds(wolf.getHunger() + " / " + wolf.getMaxHunger(), graphics).getWidth() / 2);
		final int textY = 82;
		graphics.drawString(wolf.getHunger() + " / " + wolf.getMaxHunger(), textX, textY);

		graphics.dispose();

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferedImage, "png", bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes.toByteArray();
	}

	public static BufferedImage generateAnimal(IUser user) {
		final WolfTemplate wolf = DataBase.wolfQuery(user);
		BufferedImage bufferedImage = new BufferedImage(wolf.getBase().getWidth(), wolf.getBase().getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bufferedImage.createGraphics();

		final double calcW = wolf.getBase().getWidth() * multiplier;
		final double calcH = wolf.getBase().getHeight() * multiplier;
		final int width = (int) calcW;
		final int height = (int) calcH;

		// Draw the wolf base
		graphics.drawImage(wolf.getBase(),
				0,
				0,
				width,
				height,
				null);

		// Draw the wolf cosmetics
		graphics.drawImage(wolf.getBody(),
				0,
				0,
				width,
				height,
				null);

		graphics.drawImage(wolf.getEye(),
				0,
				0,
				width,
				height,
				null);

		graphics.drawImage(wolf.getNose(),
				0,
				0,
				width,
				height,
				null);

		graphics.drawImage(wolf.getShirt(),
				0,
				0,
				width,
				height,
				null);

		graphics.drawImage(wolf.getNeck(),
				0,
				0,
				width,
				height,
				null);

		graphics.drawImage(wolf.getTail(),
				0,
				0,
				width,
				height,
				null);

		graphics.drawImage(wolf.getPaws(),
				0,
				0,
				width,
				height,
				null);

		graphics.drawImage(wolf.getHat(),
				0,
				0,
				width,
				height,
				null);

		graphics.dispose();
		return bufferedImage;
	}
}