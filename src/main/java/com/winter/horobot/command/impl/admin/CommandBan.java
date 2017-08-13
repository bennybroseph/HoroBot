package com.winter.horobot.command.impl.admin;

import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.command.Command;
import com.winter.horobot.data.Node;
import com.winter.horobot.util.AdminUtil;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;

/**
 * Created by WiNteR on 2017-08-13.
 */
public class CommandBan extends Node<Command> {

	public CommandBan() {
		super(new Command(
				"ban",
				PermissionChecks.hasPermision(Permissions.BAN),
				AdminUtil::ban
		), Collections.emptyList());
	}
}
