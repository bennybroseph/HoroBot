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

import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.command.proccessing.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MissingPermissionsException;

public class CommandRemoveRole implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_ROLES);
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 2) {
			IUser user = null;
			if (event.getMessage().getMentions().size() == 1)
				user = event.getMessage().getMentions().get(0);
			if (user == null)
				user = Utility.getFirstUser(event.getGuild(), args[0]);

			IRole role = null;
			if (event.getMessage().getRoleMentions().size() == 1)
				role = event.getMessage().getRoleMentions().get(0);
			if (role == null)
				role = Utility.getFirstRole(event.getGuild(), args[1]);

			if (user != null) {
				if (role != null) {
					if (user.getRolesForGuild(event.getMessage().getGuild()).contains(role)) {
						try {
							user.removeRole(role);
							Message.reply("user-role-removed", event.getMessage());
						} catch (MissingPermissionsException e) {
							Message.sendMessageInChannel(event.getChannel(), "missing-permissions", Utility.permissionsAsString(e.getMissingPermissions()));
						}
					} else {
						Message.reply("has-no-role", event.getMessage());
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
	}

	public String help() {
		return "remove-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.ADMIN;
	}

	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}
