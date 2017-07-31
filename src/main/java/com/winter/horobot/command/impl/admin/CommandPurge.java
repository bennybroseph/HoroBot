package com.winter.horobot.command.impl.admin;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MissingPermissionsException;

import java.util.stream.Collectors;

public class CommandPurge implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR);
	}

	@Override
	public CommandType getType() {
		return CommandType.ADMIN;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 1) {
			int messages;
			try {
				messages = Integer.parseUnsignedInt(args[0]);
			} catch (NumberFormatException e) { return; }

			try {
				event.getChannel().bulkDelete(event.getChannel().getMessageHistory(messages).stream().distinct().collect(Collectors.toList()));
				Message.sendMessageInChannel(event.getChannel(), "purged-messages", messages);
				return;
			} catch (MissingPermissionsException e) {
				Utility.sendMissingPermissions(event.getChannel(), e);
				return;
			}
		} else if (args.length == 2) {
			if (event.getMessage().getMentions().size() == 1) {
				IUser user = event.getMessage().getMentions().get(0);
				int messages;
				try {
					messages = Integer.parseUnsignedInt(args[1]);
				} catch (NumberFormatException e) { return; }

				try {
					event.getChannel().bulkDelete(event.getChannel().getMessageHistory(messages).stream().filter(message -> message.getAuthor().equals(user)).distinct().collect(Collectors.toList()));
					Message.sendMessageInChannel(event.getChannel(), "purged-messages", messages);
					return;
				} catch (MissingPermissionsException e) {
					Utility.sendMissingPermissions(event.getChannel(), e);
					return;
				}
			}
		}
		Message.sendMessageInChannel(event.getChannel(), help());
	}

	public String help() {
		return "purge-help";
	}

	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}
