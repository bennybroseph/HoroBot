package com.winter.horobot.util;

public enum FilterReason {

	MASS_USER_MENTION("Mass User Mention", 0),
	MASS_CHANNEL_MENTION("Mass Channel Mention", 1),
	MASS_ROLE_MENTION("Mass Role Mention", 2),
	SPAM("Spam", 3),
	LINK("Link", 4);

	private String humanReadable;
	private int type;

	FilterReason(String humanReadable, int type) {
		this.humanReadable = humanReadable;
		this.type = type;
	}

	public String getHumanReadable() {
		return humanReadable;
	}

	public int getType() {
		return type;
	}
}
