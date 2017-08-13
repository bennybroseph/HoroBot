package com.winter.horobot.command.impl.admin;

import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.command.Command;
import com.winter.horobot.data.Node;
import com.winter.horobot.util.AdminUtil;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;

public class CommandBan extends Node<Command> {

	public CommandBan() {
		super(new Command(
				"ban",
				"ban-help",
				PermissionChecks.hasPermision(Permissions.BAN),
				AdminUtil::ban
		), Collections.emptyList());
	}
}
