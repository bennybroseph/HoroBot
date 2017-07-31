package com.winter.horobot.command.impl.admin;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

public class CommandPrefix implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR);
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 0) {
			Message.sendRawMessageInChannel(event.getChannel(), String.format(Localisation.getMessage(event.getGuild().getStringID(), "current-prefix"), DataBase.guildQuery(event.getGuild().getStringID(), "prefix")));
		} else if(args.length == 1) {
			DataBase.insertGuild(event.getGuild().getStringID());
			DataBase.updateGuild(event.getGuild().getStringID(), "prefix", args[0]);
			Message.sendRawMessageInChannel(event.getChannel(), String.format(Localisation.getMessage(event.getGuild().getStringID(), "prefix-updated"), args[0]));
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "prefix-help";
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