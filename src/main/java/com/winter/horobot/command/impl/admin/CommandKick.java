package com.winter.horobot.command.impl.admin;

import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.command.Command;
import com.winter.horobot.data.Node;
import com.winter.horobot.util.AdminUtil;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;

public class CommandKick extends Node<Command> {

	public CommandKick() {
		super(new Command(
				"kick",
				"kick-help",
				PermissionChecks.hasPermision(Permissions.KICK),
				AdminUtil::kick
		), Collections.emptyList());
	}
}
