package com.winter.horobot.command.impl.admin;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Message;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

public class CommandPMMessage implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR);
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if (args.length > 0) {
			DataBase.insertGuild(event.getGuild().getStringID());
			if(raw.toLowerCase().equals("none")) {
				DataBase.updateGuild(event.getGuild().getStringID(), "pm", "none");
				Message.sendMessageInChannel(event.getChannel(), "pm-changed", "None");
			} else {
				DataBase.updateGuild(event.getGuild().getStringID(), "pm", raw);
				Message.sendMessageInChannel(event.getChannel(), "pm-changed", raw);
			}
		} else {
			Message.sendMessageInChannel(event.getChannel(), help());
		}
	}

	@Override
	public String help() {
		return "help-pm-message";
	}

	@Override
	public CommandType getType() {
		return CommandType.ADMIN;
	}
}