package com.winter.horobot.profile;

import com.winter.horobot.animals.Item;
import com.winter.horobot.animals.wolf.WolfCosmetics;
import com.winter.horobot.animals.wolf.WolfProfileBuilder;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.obj.IUser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ProfileTemplate {

	private final String name;
	private final String description;
	private final int level;
	private final int xp;
	private final int maxXp;
	private final int foxCoins;
	private final boolean notifications;
	private BufferedImage background;
	private BufferedImage template;
	private BufferedImage wolf;
	private BufferedImage lvlTemplate;

	public ProfileTemplate(IUser user, String description, int level, int xp, int maxXp, int foxCoins, String background, boolean notifications) {
		this.name = user.getName();
		this.description = description;
		this.level = level;
		this.xp = xp;
		this.maxXp = maxXp;
		this.foxCoins = foxCoins;
		this.notifications = notifications;
		try {
			try {
				this.background = ImageIO.read(getClass().getResourceAsStream(Utility.getItemByName(background.toUpperCase()).getFile()));
			} catch (NullPointerException e) {
				this.background = ImageIO.read(getClass().getResourceAsStream("/wolf/bg/default-bg.png"));
			}
			this.template = ImageIO.read(getClass().getResourceAsStream("/profile/template.png"));
			this.wolf = WolfProfileBuilder.generateAnimal(user);
			this.lvlTemplate = ImageIO.read(getClass().getResourceAsStream("/profile/lvl-template.png"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getLevel() {
		return level;
	}

	public int getXp() {
		return xp;
	}

	public int getMaxXp() {
		return maxXp;
	}

	public int getFoxCoins() {
		return foxCoins;
	}

	public boolean getNotifications() { return notifications; }

	public BufferedImage getBackground() {
		return background;
	}

	public BufferedImage getTemplate() { return template; }

	public BufferedImage getWolf() { return wolf; }

	public BufferedImage getLvlTemplate() { return lvlTemplate; }
}