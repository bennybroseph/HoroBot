package com.winter.horobot.command.impl.admin;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Message;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

public class CommandAutoRole implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR);
	}

	@Override
	public CommandType getType() {
		return CommandType.ADMIN;
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 1) {
			IRole role = null;
			try {
				role = event.getGuild().getRoleByID(Long.parseUnsignedLong(args[0]));
			} catch (NumberFormatException exception) { }
			if(event.getMessage().getRoleMentions().size() == 1 && role == null)
				role = event.getMessage().getRoleMentions().get(0);
			try {
				if (role == null) role = event.getGuild().getRolesByName(args[0]).get(0);
			} catch (IndexOutOfBoundsException e) { }

			if(role != null) {
				DataBase.updateGuild(event.getGuild().getStringID(), "role", role.getStringID());
				Message.sendMessageInChannel(event.getChannel(), "role-changed", role.getName());
			} else {
				Message.sendMessageInChannel(event.getChannel(), "no-role-set");
			}
		} else {
			Message.sendMessageInChannel(event.getChannel(), help());
		}
	}

	@Override
	public String help() {
		return "help-autoroll";
	}
}
