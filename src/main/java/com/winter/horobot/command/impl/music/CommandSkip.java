package com.winter.horobot.command.impl.music;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.util.music.MusicUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

public class CommandSkip implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return (event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR));
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 0) {
			if(MusicUtils.getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack() != null) {
				MusicUtils.skipTrack(event.getChannel());
				Message.sendRawMessageInChannel(event.getChannel(), Localisation.getMessage(event.getGuild().getStringID(), "track-skipped"));
			} else {
				Message.sendMessageInChannel(event.getChannel(), "nothing-playing");
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "skip-help";
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