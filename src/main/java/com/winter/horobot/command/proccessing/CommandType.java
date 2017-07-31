package com.winter.horobot.command.proccessing;

import com.winter.horobot.core.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum CommandType {
	ADMIN(0, "Admin"),
	ANIMAL(1, "Template"),
	DEVELOPER(2, "Developer Only"),
	FUN(3, "Fun"),
	IMAGE(4, "Image"),
	MISC(5, "Miscellaneous"),
	MUSIC(6, "Music"),
	PROFILE(7, "Profile"),
	UTILITY(8, "Utility");

	private final int type;
	private final String description;
	CommandType(int type, String description) {
		this.type = type;
		this.description = description;
	}

	int getType() {
			return this.type;
		}

	String getDescription() {
			return this.description;
		}

	public static CommandType[] getTypes() {
		return new CommandType[]{ADMIN, ANIMAL, DEVELOPER, FUN, IMAGE, MISC, MUSIC, PROFILE, UTILITY};
	}

	public static int getTypeCount() {
		return new CommandType[]{ADMIN, ANIMAL, DEVELOPER, FUN, IMAGE, MISC, MUSIC, PROFILE, UTILITY}.length;
	}

	public List<String> getCommands(CommandType type) {
		return new ArrayList<>(Main.commands.keySet()).stream().filter(command -> Main.commands.get(command).getType() == type).collect(Collectors.toList());
	}
}