package com.winter.horobot.command.impl.status;

import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.command.Command;
import com.winter.horobot.data.Node;
import com.winter.horobot.data.locale.Localisation;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;

public class CommandHi extends Node<Command> {

	public CommandHi() {
		super(new Command(
				"hi",
				PermissionChecks.hasPermision(Permissions.SEND_MESSAGES),
				e -> {
					e.getChannel().sendMessage(Localisation.of(e.getGuild(), "hello"));
					return true;
				}
		), Collections.emptyList());
	}
}