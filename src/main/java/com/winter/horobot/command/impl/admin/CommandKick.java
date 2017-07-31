package com.winter.horobot.command.impl.admin;

import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.command.proccessing.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class CommandKick implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.KICK);
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length >= 1) {
			IUser user = null;
			if (event.getMessage().getMentions().size() == 1) {
				user = event.getMessage().getMentions().get(0);
			}
			if (user == null) {
				try {
					user = event.getGuild().getUserByID(Long.parseUnsignedLong(args[0]));
				} catch (NumberFormatException e) { }
			}
			if (user == null) {
				if (event.getGuild().getUsersByName(args[0]).size() == 1) {
					user = event.getGuild().getUsersByName(args[0]).get(0);
				}
			}

			if (user != null) {
				String reason;
				if (args.length == 1) {
					reason = "No reason specified";
				} else {
					reason = raw.substring(args[0].length() + 1, raw.length());
				}

				IChannel channel = Utility.getLogChannel(event.getGuild());
				if (channel == null) channel = event.getChannel();
				Message.sendEmbed(channel, "", Utility.kickUser(event.getGuild(), user, event.getClient().getOurUser(), reason), false);
				Message.sendPM(user, "kicked-from", event.getGuild().getName(), reason);
			} else {
				Message.reply("user-not-found", event.getMessage());
			}
		} else {
			Message.sendMessageInChannel(event.getChannel(), help());
		}
	}

	public String help() {
		return "kick-help";
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
