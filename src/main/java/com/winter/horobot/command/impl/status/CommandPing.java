package com.winter.horobot.command.impl.status;

import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.command.Command;
import com.winter.horobot.data.Node;
import com.winter.horobot.util.StatusUtil;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;
import java.util.HashSet;

public class CommandPing extends Node<Command> {

	public CommandPing() {
		super(new Command(
				"ping",
				PermissionChecks.hasPermision(Permissions.SEND_MESSAGES),
				StatusUtil::ping,
				new HashSet<>(Collections.singleton("pong"))
		), Collections.emptyList());
	}
}
