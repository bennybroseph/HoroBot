package com.winter.horobot.command.impl.admin;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Message;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

public class CommandWelcomeMessage implements Command {

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
		if(args.length > 0) {
			if(raw.toLowerCase().equals("none")) {
				DataBase.updateGuild(event.getGuild().getStringID(), "welcome", "none");
				Message.sendMessageInChannel(event.getChannel(), "welcome-changed", "None");
			} else {
				DataBase.updateGuild(event.getGuild().getStringID(), "welcome", raw);
				Message.sendMessageInChannel(event.getChannel(), "welcome-changed", raw);
			}
		} else {
			Message.sendMessageInChannel(event.getChannel(), help());
		}
	}

	@Override
	public String help() {
		return "help-welcomemessage";
	}
}
