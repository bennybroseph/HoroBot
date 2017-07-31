package com.winter.horobot.command.impl.admin;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.Map;

public class CommandTag implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if (args.length == 0) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.withColor(Color.CYAN);
			builder.withAuthorName(event.getGuild().getName() + "'s Tags");
			builder.withAuthorIcon(event.getGuild().getIconURL());
			builder.withThumbnail(event.getGuild().getIconURL());

			Map<String, String> tags = DataBase.queryTags(event.getGuild());
			if (tags == null) {
				builder.withDescription(Localisation.getMessage(event.getGuild().getStringID(), "html-error"));
				Message.sendEmbed(event.getChannel(), "", builder.build(), false);
				return;
			}
			if (tags.size() == 0) {
				builder.withDescription(Localisation.getMessage(event.getGuild().getStringID(), "tags-empty"));
				Message.sendEmbed(event.getChannel(), "", builder.build(), false);
				return;
			}
			for (Map.Entry<String, String> tag : tags.entrySet()) {
				builder.appendField(tag.getKey(), tag.getValue(), false);
			}
			Message.sendEmbed(event.getChannel(), "", builder.build(), false);
		}

		if (args.length == 1) {
			if(args[0].equals("help")) {
				Message.sendMessageInChannel(event.getChannel(), help());
				return;
			}
			String content = DataBase.queryTag(event.getGuild().getStringID(), args[0]);

			if (content != null) {
				EmbedBuilder builder = new EmbedBuilder();
				builder.withColor(Color.CYAN);
				builder.withAuthorName("Requested By " + event.getAuthor().getName());
				builder.withAuthorIcon(Utility.getAvatar(event.getAuthor()));

				builder.withTitle(args[0]);
				builder.withDescription(content);

				Message.sendEmbed(event.getChannel(), "", builder.build(), false);
			} else {
				Message.sendMessageInChannel(event.getChannel(), "no-tag");
			}
		}

		if (args.length >= 2) {
			switch (args[0]) {
				case "add": {
					if (event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_SERVER)) {
						if (!args[1].equals("add") && !args[1].equals("remove") && !args[1].equals("help")) {
							DataBase.insertTag(event.getGuild().getStringID(), args[1], raw.substring(args[0].length() + args[1].length() + 1, raw.length()));

							EmbedBuilder builder = new EmbedBuilder();
							builder.withColor(Color.GREEN);
							builder.withAuthorName("Tag Added By " + event.getAuthor().getName());
							builder.withAuthorIcon(Utility.getAvatar(event.getAuthor()));
							builder.withTitle("Tag Name: " + args[1]);
							builder.withDescription(raw.substring(args[0].length() + args[1].length() + 1, raw.length()));

							Message.sendEmbed(event.getChannel(), "", builder.build(), false);
						} else {
							Message.sendMessageInChannel(event.getChannel(), "invalid-tag-name");
						}
					}
					break;
				}
				case "remove": {
					if (event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_SERVER)) {
						DataBase.removeTag(event.getGuild().getStringID(), args[1]);

						EmbedBuilder builder = new EmbedBuilder();
						builder.withColor(Color.RED);
						builder.withAuthorName("Tag Removed By " + event.getAuthor().getName());
						builder.withAuthorIcon(Utility.getAvatar(event.getAuthor()));
						builder.withTitle("Tag Name: " + args[1]);

						Message.sendEmbed(event.getChannel(), "", builder.build(), false);
					}
					break;
				}
			}
		}
	}

	@Override
	public String help() {
		return "help-tag";
	}

	@Override
	public CommandType getType() {
		return CommandType.ADMIN;
	}
}
