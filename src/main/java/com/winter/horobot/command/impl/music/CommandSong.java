package com.winter.horobot.command.impl.music;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.util.music.MusicUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

public class CommandSong implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 0) {
			if (event.getGuild().getConnectedVoiceChannel() != null) {
				if(MusicUtils.getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack() != null) {
					AudioPlayer player = MusicUtils.getGuildAudioPlayer(event.getGuild()).player;
					String title = player.getPlayingTrack().getInfo().title;
					String author = player.getPlayingTrack().getInfo().author;
					String uri = player.getPlayingTrack().getInfo().uri;
					String length = formatDuration(player.getPlayingTrack());

					StringBuilder string = new StringBuilder();
					string.append("**Title:** " + title + "\n");
					string.append("**Author:** " + author + "\n");
					string.append("**Length:** " + length + "\n");
					string.append("**URI:** " + uri);

					EmbedBuilder builder = new EmbedBuilder();
					builder.withColor(Color.CYAN);
					builder.withAuthorName("Requested by @" + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
					builder.withAuthorIcon(Utility.getAvatar(event.getAuthor()));
					builder.appendField("Current Song", string.toString(), false);

					Message.sendEmbed(event.getChannel(), "", builder.build(), false);
				} else {
					Message.sendRawMessageInChannel(event.getChannel(), Localisation.getMessage(event.getGuild().getStringID(), "no-song-playing"));
				}
			} else {
				Message.sendRawMessageInChannel(event.getChannel(), Localisation.getMessage(event.getChannel().getGuild().getStringID(), "not-in-channel"));
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "song-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.MUSIC;
	}

	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}

	public static String formatDuration(AudioTrack track) {
		long totalSeconds = track.getDuration() / 1000;
		long seconds = totalSeconds % 60;
		long minutes = (totalSeconds / 60) % 60;
		long hours = (totalSeconds / 3600);
		return (hours > 0 ? (hours < 10 ? "0" + hours : hours) + ":" : "")
				+ (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
	}
}