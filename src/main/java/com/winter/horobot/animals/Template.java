package com.winter.horobot.animals;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Template {
	private final String name;
	private final int level;
	private final int hunger;
	private final int maxHunger;
	private final int fedTimes;
	private BufferedImage background;
	private BufferedImage hat;
	private BufferedImage body;
	private BufferedImage paws;
	private BufferedImage tail;
	private BufferedImage shirt;
	private BufferedImage nose;
	private BufferedImage eye;
	private BufferedImage neck;
	private BufferedImage base;
	private BufferedImage template;

	public Template(String name, int level, int hunger, int maxHunger, int fedTimes, String background, String hat, String body, String paws, String tail, String shirt, String nose, String eye, String neck, String basePath, String templatePath) {
		this.name = name;
		this.level = level;
		this.hunger = hunger;
		this.maxHunger = maxHunger;
		this.fedTimes = fedTimes;
		try {
			this.background = ImageIO.read(getClass().getResourceAsStream(background));
			this.hat = ImageIO.read(getClass().getResourceAsStream(hat));
			this.body = ImageIO.read(getClass().getResourceAsStream(body));
			this.paws = ImageIO.read(getClass().getResourceAsStream(paws));
			this.tail = ImageIO.read(getClass().getResourceAsStream(tail));
			this.shirt = ImageIO.read(getClass().getResourceAsStream(shirt));
			this.nose = ImageIO.read(getClass().getResourceAsStream(nose));
			this.eye = ImageIO.read(getClass().getResourceAsStream(eye));
			this.neck = ImageIO.read(getClass().getResourceAsStream(neck));
			this.base = ImageIO.read(getClass().getResourceAsStream(basePath));
			this.template = ImageIO.read(getClass().getResourceAsStream(templatePath));
		} catch (IOException e) { }
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public int getHunger() {
		return hunger;
	}

	public int getMaxHunger() {
		return maxHunger;
	}

	public int getFedTimes() {
		return fedTimes;
	}

	public BufferedImage getBackground() {
		return background;
	}

	public BufferedImage getHat() {
		return hat;
	}

	public BufferedImage getBody() { return body; }

	public BufferedImage getPaws() {
		return paws;
	}

	public BufferedImage getTail() {
		return tail;
	}

	public BufferedImage getShirt() {
		return shirt;
	}

	public BufferedImage getNose() {
		return nose;
	}

	public BufferedImage getEye() {
		return eye;
	}

	public BufferedImage getNeck() {
		return neck;
	}

	public BufferedImage getBase() { return base; }

	public BufferedImage getTemplate() { return template; }
}