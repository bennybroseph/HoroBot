package com.winter.horobot.command.impl.music;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.HTTPHandler;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.util.music.MusicUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.net.URL;

public class CommandPlay implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length >= 1) {
			if (event.getGuild().getConnectedVoiceChannel() == null) {
				if(event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel() != null) {
					event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel().join();
					Message.sendMessageInChannel(event.getChannel(), "joined-voice");
				} else {
					Message.sendMessageInChannel(event.getChannel(), "not-in-channel");
					return;
				}
			}
			try {
				URL url = new URL(raw);
				MusicUtils.loadAndPlay(event, event.getGuild().getConnectedVoiceChannel(), args[0]);
			} catch(Exception e) {
				String link;
				try {
					link = HTTPHandler.searchYouTube(raw);
				} catch (Exception ee) {
					Message.sendMessageInChannel(event.getChannel(), "html-error");
					return;
				}
				if(link != null) {
					MusicUtils.loadAndPlay(event, event.getGuild().getConnectedVoiceChannel(), link);
				} else {
					Message.sendRawMessageInChannel(event.getChannel(), Localisation.getMessage(event.getChannel().getGuild().getStringID(), "html-no-results"));
				}
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "play-help";
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