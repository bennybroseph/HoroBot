package com.winter.horobot.command.impl.admin;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Message;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

public class CommandLogChannel implements Command {

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
			if(args[0].toLowerCase().equals("none")) {
				DataBase.insertChannel(event.getChannel().getStringID(), "none");
				DataBase.updateChannel(event.getChannel().getStringID(), "none");
				Message.sendMessageInChannel(event.getChannel(), "log-channel-set", "None");
			} else {
				IChannel channel = null;
				try {
					channel = event.getGuild().getChannelByID(Long.parseUnsignedLong(args[0]));
				} catch (NumberFormatException e) { }
				if (event.getMessage().getChannelMentions().size() == 1 && channel == null)
					channel = event.getMessage().getChannelMentions().get(0);
				try {
					if (channel == null) channel = event.getGuild().getChannelsByName(args[0]).get(0);
				} catch (IndexOutOfBoundsException e) {}

				if (channel != null) {
					for (IChannel temp : event.getGuild().getChannels()) {
						if (DataBase.channelQuery(temp.getStringID()).equals("log")) {
							DataBase.insertChannel(temp.getStringID(), "none");
							DataBase.updateChannel(temp.getStringID(), "none");
						}
					}
					DataBase.insertChannel(channel.getStringID(), "log");
					DataBase.updateChannel(channel.getStringID(), "log");
					Message.sendMessageInChannel(event.getChannel(), "log-channel-set", channel.getName());
				} else {
					Message.sendMessageInChannel(event.getChannel(), "no-channel");
				}
			}
		} else {
			Message.sendMessageInChannel(event.getChannel(), help());
		}
	}

	@Override
	public String help() {
		return "help-logchannel";
	}
}
