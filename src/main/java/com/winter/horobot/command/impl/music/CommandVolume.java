package com.winter.horobot.command.impl.music;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.music.MusicUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

public class CommandVolume implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR);
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 1) {
			int volume;
			try {
				volume = Integer.parseInt(args[0]);
				if (volume > 100 || volume < 0) throw new Exception();
			} catch (Exception e) {
				Message.sendMessageInChannel(event.getChannel(), "no-number");
				return;
			}
			MusicUtils.getGuildAudioPlayer(event.getGuild()).player.setVolume(volume);
			Message.sendMessageInChannel(event.getChannel(), "volume-changed", volume + "%");
		} else {
			Message.sendMessageInChannel(event.getChannel(), help());
		}
	}

	@Override
	public String help() {
		return "volume-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.MUSIC;
	}
}
