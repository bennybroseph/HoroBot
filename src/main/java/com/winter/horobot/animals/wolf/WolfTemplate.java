package com.winter.horobot.animals.wolf;

import com.winter.horobot.animals.Template;

public class WolfTemplate extends Template {

	public WolfTemplate(String name, int level, int hunger, int maxHunger, int fedTimes, String background, String hat, String body, String paws, String tail, String shirt, String nose, String eye, String neck) {
		super(name, level, hunger, maxHunger, fedTimes, background, hat, body, paws, tail, shirt, nose, eye, neck, "/wolf/wolf-base.png", "/wolf/template.png");
	}
}