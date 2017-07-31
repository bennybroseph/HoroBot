package com.winter.horobot.command.impl.music;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.music.MusicUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

public class CommandClear implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return (event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.ADMINISTRATOR));
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		MusicUtils.getGuildAudioPlayer(event.getGuild()).scheduler.clearQueue();
		Message.sendMessageInChannel(event.getChannel(), "queue-success");
	}

	@Override
	public String help() {
		return "clear-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.MUSIC;
	}
}
