package com.winter.horobot.command.impl.developer;

import com.winter.horobot.Main;
import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.command.Command;
import com.winter.horobot.data.Node;
import com.winter.horobot.util.MessageUtil;

import java.util.Arrays;
import java.util.Collections;

public class CommandSet extends Node<Command> {

	public CommandSet() {
		super(new Command(
				"set",
				"set-help",
				PermissionChecks.isGlobal(),
				e -> false
		), Arrays.asList(
				new Node<>(new Command(
						"playing",
						"",
						PermissionChecks.isGlobal(),
						e -> {
							Main.getClient().changePlayingText(MessageUtil.args(e.getMessage()).substring("set playing".length()));
							return true;
						}
				), Collections.emptyList())
		));
	}
}