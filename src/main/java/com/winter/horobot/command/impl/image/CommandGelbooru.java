package com.winter.horobot.command.impl.image;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.HTTPHandler;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.net.URL;

public class CommandGelbooru implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getChannel().isNSFW();
	}

	@Override
	public CommandType getType() {
		return CommandType.IMAGE;
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length > 0) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.withAuthorIcon(Utility.getAvatar(event.getAuthor()));
			builder.withAuthorName(event.getAuthor().getName());
			builder.withColor(Color.CYAN);
			try {
				String image = HTTPHandler.requestGelbooru(args);
				URL url = new URL(image);
				builder.withImage(image);
				Message.sendEmbed(event.getChannel(), "", builder.build(), false);
			} catch (Exception e) {
				Message.sendMessageInChannel(event.getChannel(), "html-error");
			}
		} else {
			Message.sendMessageInChannel(event.getChannel(), help());
		}
	}

	@Override
	public String help() {
		return "help-gelbooru";
	}
}