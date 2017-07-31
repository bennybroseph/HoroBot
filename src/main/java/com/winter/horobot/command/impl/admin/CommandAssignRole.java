/*
	HoroBot - An open-source Discord bot
	Copyright (C) 2017	WiNteR

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.winter.horobot.command.impl.admin;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class CommandAssignRole implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_ROLES);
	}

	@Override
	public CommandType getType() {
		return CommandType.ADMIN;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 2) {
			if (Utility.checkUserPermission(event.getGuild(), event.getClient().getOurUser(), Permissions.MANAGE_ROLES)) {
				IUser user = null;
				if (event.getMessage().getMentions().size() == 1) {
					user = event.getMessage().getMentions().get(0);
				}
				if (user == null) {
					try {
						user = event.getGuild().getUserByID(Long.parseUnsignedLong(args[0]));
					} catch (NumberFormatException ignored) { }
				}
				if (user == null && event.getGuild().getUsersByName(args[0]).size() == 1) {
					user = event.getGuild().getUsersByName(args[0]).get(0);
				}

				IRole role = null;
				if (event.getMessage().getRoleMentions().size() == 1) {
					role = event.getMessage().getRoleMentions().get(0);
				}
				if (role == null) {
					try {
						role = event.getGuild().getRoleByID(Long.parseUnsignedLong(args[1]));
					} catch (NumberFormatException ignored) { }
				}
				if (role == null && event.getGuild().getRolesByName(args[1]).size() == 1) {
					role = event.getGuild().getRolesByName(args[1]).get(0);
				}

				if (role != null) {
					if (user != null) {
						if (!user.getRolesForGuild(event.getGuild()).contains(role)) {
							try {
								user.addRole(role);
								Message.reply("user-role-added", event.getMessage());
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Message.reply("has-role", event.getMessage());
						}
					} else {
						Message.reply("user-not-found", event.getMessage());
					}
				} else {
					Message.reply("no-role", event.getMessage());
				}
			} else {
				Message.sendMessageInChannel(event.getChannel(), "missing-manage-roles-perm");
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "assign-help";
	}

	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}
