package com.winter.horobot.command.impl.admin;

import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.command.Command;
import com.winter.horobot.data.Node;
import com.winter.horobot.data.cache.HoroCache;
import com.winter.horobot.exceptions.ErrorHandler;
import com.winter.horobot.exceptions.UpdateFailedException;
import com.winter.horobot.util.GuildUtil;
import com.winter.horobot.util.MessageUtil;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Arrays;
import java.util.Collections;

public class CommandPrefix extends Node<Command> {

	public CommandPrefix() {
		super(new Command(
				"prefix",
				"kick-help",
				PermissionChecks.hasPermision(Permissions.SEND_MESSAGES),
				e -> {
					MessageUtil.sendMessage(e.getChannel(), "prefixes", Arrays.toString(GuildUtil.getPrefixes(e.getGuild()).toArray()));
					return true;
				}
		), Arrays.asList(
				new Node<>(new Command(
						"add",
						"",
						PermissionChecks.hasPermision(Permissions.MANAGE_SERVER),
						e -> {
							try {
								HoroCache.get(e.getGuild()).addPrefix(MessageUtil.args(e.getMessage()).substring("prefix add ".length()));
								MessageUtil.sendMessage(e.getChannel(), "added-prefix");
							} catch (UpdateFailedException ex) {
								ErrorHandler.log(ex, e.getChannel());
							}
							return true;
						}
				), Collections.emptyList()),
				new Node<>(new Command(
						"remove",
						"",
						PermissionChecks.hasPermision(Permissions.MANAGE_SERVER),
						e -> {
							try {
								HoroCache.get(e.getGuild()).removePrefix(MessageUtil.args(e.getMessage()).substring("prefix remove ".length()));
								MessageUtil.sendMessage(e.getChannel(), "removed-prefix");
							} catch (UpdateFailedException ex) {
								ErrorHandler.log(ex, e.getChannel());
							}
							return true;
						}), Collections.emptyList())));
	}
}
