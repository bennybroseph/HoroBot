package com.winter.horobot.command.impl.status;

import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.command.Command;
import com.winter.horobot.command.Commands;
import com.winter.horobot.data.Node;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;

public class CommandHelp extends Node<Command> {

	public CommandHelp() {
		super(new Command(
				"help",
				PermissionChecks.hasPermision(Permissions.SEND_MESSAGES),
				Commands::sendHelp
		), Collections.emptyList());
	}
}
