package com.winter.horobot.command.impl.music;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;

public class CommandJoin implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR);
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if (Utility.checkUserPermission(event.getGuild(), event.getClient().getOurUser(), Permissions.VOICE_CONNECT)) {
			if (args.length == 1) {
				IVoiceChannel channel = null;
				try {
					channel = event.getGuild().getVoiceChannelByID(Long.parseUnsignedLong(args[0]));
				} catch (NumberFormatException e) { }
				if (channel == null) {
					if (event.getGuild().getVoiceChannelsByName(args[0]).size() == 1) {
						channel = event.getGuild().getVoiceChannelsByName(args[0]).get(0);
					}
				}

				if (channel != null) {
					channel.join();
					Message.sendRawMessageInChannel(event.getChannel(), Localisation.getMessage(event.getGuild().getStringID(), "joined-voice"));
				} else {
					Message.reply("no-voice-channel", event.getMessage());
				}
			} else {
				if (event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel() != null) {
					event.getGuild().getVoiceChannelByID(Long.parseUnsignedLong(event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel().getStringID())).join();
					Message.sendRawMessageInChannel(event.getChannel(), Localisation.getMessage(event.getGuild().getStringID(), "joined-voice"));
				} else {
					Message.reply("no-voice-channel", event.getMessage());
				}
			}
		} else {
			Message.sendMessageInChannel(event.getChannel(), "missing-voice-perm");
		}
	}

	public String help() {
		return "join-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.MUSIC;
	}

	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}