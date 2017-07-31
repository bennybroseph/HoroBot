package com.winter.horobot.command.impl.music;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.util.music.MusicUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class CommandUnpause implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 0) {
			if (event.getGuild().getConnectedVoiceChannel() != null) {
				MusicUtils.unpause(event.getGuild(), MusicUtils.getGuildAudioPlayer(event.getGuild()));
				Message.sendRawMessageInChannel(event.getChannel(), Localisation.getMessage(event.getChannel().getGuild().getStringID(), "resumed"));
			} else {
				Message.sendRawMessageInChannel(event.getChannel(), Localisation.getMessage(event.getChannel().getGuild().getStringID(), "not-in-channel"));
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "unpause-help";
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