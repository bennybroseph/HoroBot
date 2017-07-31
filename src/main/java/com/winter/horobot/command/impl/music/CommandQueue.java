package com.winter.horobot.command.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.util.music.GuildMusicManager;
import com.winter.horobot.util.music.MusicUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.Iterator;

public class CommandQueue implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		GuildMusicManager manager = MusicUtils.getGuildAudioPlayer(event.getGuild());
		if(manager.scheduler.getQueueSize() > 0) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.withColor(Color.CYAN);
			builder.withAuthorIcon(Utility.getAvatar(event.getAuthor()));
			builder.withAuthorName("Requested by @" + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());

			Iterator<AudioTrack> iterator = manager.scheduler.getQueue().iterator();
			for(int i = 0; i < 10; i++) {
				if(iterator.hasNext()) {
					AudioTrack track = iterator.next();
					String title = track.getInfo().title;
					String author = track.getInfo().author;
					String uri = track.getInfo().uri;
					String length = formatDuration(track);

					String string = "**Author:** " + author + "\n";
					string += "**Length:** " + length + "\n";
					string += "**URI:** " + uri;
					builder.appendField(title, string, false);
				}
			}
			Message.sendEmbed(event.getChannel(), Localisation.getMessage(event.getGuild().getStringID(), "heres-queue"), builder.build(), false);
		} else {
			Message.sendMessageInChannel(event.getChannel(), "queue-empty");
		}
	}

	private static String formatDuration(AudioTrack track) {
		long totalSeconds = track.getDuration() / 1000;
		long seconds = totalSeconds % 60;
		long minutes = (totalSeconds / 60) % 60;
		long hours = (totalSeconds / 3600);
		return (hours > 0 ? (hours < 10 ? "0" + hours : hours) + ":" : "")
				+ (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
	}

	@Override
	public String help() {
		return "queue-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.MUSIC;
	}
}
