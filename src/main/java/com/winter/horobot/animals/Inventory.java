package com.winter.horobot.animals;

import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class Inventory {

	private final IUser holder;
	private final List<Item> backgrounds;
	private final List<Item> hats;
	private final List<Item> bodies;
	private final List<Item> paws;
	private final List<Item> tails;
	private final List<Item> shirts;
	private final List<Item> noses;
	private final List<Item> eyes;
	private final List<Item> neck;

	public Inventory(IUser holder, List<Item> backgrounds, List<Item> hats, List<Item> bodies, List<Item> paws, List<Item> tails, List<Item> shirts, List<Item> noses, List<Item> eyes, List<Item> neck) {
		this.holder = holder;
		this.backgrounds = backgrounds;
		this.hats = hats;
		this.bodies = bodies;
		this.paws = paws;
		this.tails = tails;
		this.shirts = shirts;
		this.noses = noses;
		this.eyes = eyes;
		this.neck = neck;
	}

	public IUser getHolder() {
		return holder;
	}

	public List<Item> getBackgrounds() {
		return backgrounds;
	}

	public List<Item> getHats() {
		return hats;
	}

	public List<Item> getBodies() {
		return bodies;
	}

	public List<Item> getPaws() {
		return paws;
	}

	public List<Item> getTails() {
		return tails;
	}

	public List<Item> getShirts() {
		return shirts;
	}

	public List<Item> getNoses() {
		return noses;
	}

	public List<Item> getEyes() {
		return eyes;
	}

	public List<Item> getNeck() {
		return neck;
	}
}
