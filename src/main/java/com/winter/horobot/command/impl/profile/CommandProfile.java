package com.winter.horobot.command.impl.profile;

import com.winter.horobot.animals.Item;
import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.profile.ProfileBuilder;
import com.winter.horobot.util.Cooldowns;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandProfile implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 0) {
			if(!Cooldowns.onCooldown("profile-stats-" + event.getAuthor().getStringID(), event.getAuthor())) {
				Cooldowns.putOnCooldown("profile-stats-" + event.getAuthor().getStringID(), event.getAuthor(), 10000);
				DataBase.insertUser(event.getAuthor());
				Message.sendFile(
						event.getChannel(),
						ProfileBuilder.generateEmbed(
								event.getAuthor()),
						"Here's your profile",
						"profile.png",
						new ByteArrayInputStream(ProfileBuilder.generateProfileImage(
								event.getAuthor())));
			} else {
				Message.sendMessageInChannel(event.getChannel(), "on-cooldown2", Utility.formatTime(Cooldowns.getRemaining("profile-stats-" + event.getAuthor().getStringID(), event.getAuthor())));
			}
		} else if(args.length >= 1) {
			if (event.getMessage().getMentions().size() == 1) {
				if(!event.getMessage().getMentions().get(0).isBot()) {
					if (!Cooldowns.onCooldown("profile-stats-" + event.getAuthor().getStringID(), event.getAuthor())) {
						Cooldowns.putOnCooldown("profile-stats-" + event.getAuthor().getStringID(), event.getAuthor(), 10000);
						DataBase.insertUser(event.getMessage().getMentions().get(0));
						Message.sendFile(
								event.getChannel(),
								ProfileBuilder.generateEmbed(
										event.getMessage().getMentions().get(0)),
								"Here's **" + event.getMessage().getMentions().get(0).getName() + "**'s profile!",
								"profile.png",
								new ByteArrayInputStream(ProfileBuilder.generateProfileImage(
										event.getMessage().getMentions().get(0))));
					} else {
						Message.sendMessageInChannel(event.getChannel(), "on-cooldown2", Utility.formatTime(Cooldowns.getRemaining("profile-stats-" + event.getAuthor().getStringID(), event.getAuthor())));
					}
				} else {
					Message.sendMessageInChannel(event.getChannel(), "bot-user");
				}
			} else {
				switch (args[0]) {
					case "background": {
						String temp = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
						Item item = DataBase.queryItem(event.getAuthor(), temp);
						if (item != null) {
							if (item.getType() == 0) {
								DataBase.updateUser(event.getAuthor(), "background", temp);
								Message.sendMessageInChannel(event.getChannel(), "background-updated", temp);
							} else {
								Message.sendMessageInChannel(event.getChannel(), "invalid-item");
							}
						} else {
							Message.sendMessageInChannel(event.getChannel(), "no-item");
						}
						break;
					}
					case "info": {
						String temp = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
						if (temp.length() <= 54) {
							DataBase.updateUser(event.getAuthor(), "description", temp);
							Message.sendMessageInChannel(event.getChannel(), "desc-changed");
						} else {
							Message.sendMessageInChannel(event.getChannel(), "desc-too-long");
						}
						break;
					}
					case "ranking": {
						String result = DataBase.queryRanks(10);
						EmbedBuilder builder = new EmbedBuilder();
						builder.withAuthorIcon(Utility.getAvatar(event.getAuthor()));
						builder.withAuthorName(event.getAuthor().getName());
						builder.withColor(Color.CYAN);
						builder.appendField("Global Ranking", result, false);

						Message.sendEmbed(event.getChannel(), "Here's the current ranking chart", builder.build(), false);
						break;
					}
					case "help": {
						Message.sendMessageInChannel(event.getChannel(), help());
						break;
					}
					default: {
						Message.sendMessageInChannel(event.getChannel(), "no-sub-command");
						break;
					}
				}
			}
		}
	}

	@Override
	public String help() {
		return "profile-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.PROFILE;
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}