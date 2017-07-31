package com.winter.horobot.command.impl.admin;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Map;

public class CommandBlackList implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getPermissionsForGuild(event.getGuild()).contains(Permissions.MANAGE_SERVER);
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if (args.length == 0) {
			EmbedBuilder builder = new EmbedBuilder();
			builder.withColor(Color.CYAN);
			builder.withAuthorName(event.getGuild().getName() + "'s Blacklist");
			builder.withAuthorIcon(event.getGuild().getIconURL());
			builder.withThumbnail(event.getGuild().getIconURL());

			Map<String, String> blacklist = DataBase.queryBlacklist(event.getGuild());
			if (blacklist == null) {
				builder.withDescription(Localisation.getMessage(event.getGuild().getStringID(), "html-error"));
				Message.sendEmbed(event.getChannel(), "", builder.build(), false);
				return;
			}
			if (blacklist.size() == 0) {
				builder.withDescription(Localisation.getMessage(event.getGuild().getStringID(), "blacklist-empty"));
				Message.sendEmbed(event.getChannel(), "", builder.build(), false);
				return;
			}
			for (Map.Entry<String, String> entry : blacklist.entrySet()) {
				IUser user = event.getClient().getUserByID(Long.parseUnsignedLong(entry.getKey()));
				IUser by = event.getClient().getUserByID(Long.parseUnsignedLong(entry.getValue()));
				try {
					builder.appendField(user.getName(),
							"**ID:** " + entry.getKey() + "\n\n" +
									"**Blacklisted By:** " + by.getName() + "" + "\n" +
									"**ID:** " + entry.getValue(),
							true);
				} catch (NullPointerException e) {
					builder.appendField("Unknown",
							"**ID:** " + entry.getKey() + "\n\n" +
									"**Blacklisted By:** " + by.getName() + "" + "\n" +
									"**ID:** " + entry.getValue(),
							true);
				}
			}
			Message.sendEmbed(event.getChannel(), "", builder.build(), false);
		} else if (args.length > 0) {
			switch (args[0]) {
				case "add": {
					if(args.length == 2) {
						IUser user = null;
						String id = null;
						try {
							user = event.getClient().getUserByID(Long.parseUnsignedLong(args[1]));
						} catch (NumberFormatException ignored) { }
						if (user == null && event.getMessage().getMentions().size() == 1)
							user = event.getMessage().getMentions().get(0);
						if (user == null) {
							try {
								Long.parseUnsignedLong(args[1]);
								id = args[1];
							} catch (NumberFormatException ignored) { }
						}

						if (user != null) {
							DataBase.insertBlacklistEntry(event.getGuild().getStringID(), user.getStringID(), event.getAuthor().getStringID());

							EmbedBuilder builder = new EmbedBuilder();
							builder.withAuthorIcon(Utility.getAvatar(user));
							builder.withAuthorName("User " + user.getName() + " was blacklisted");
							builder.withColor(Color.RED);
							builder.withTimestamp(LocalDateTime.now());
							builder.withThumbnail(Utility.getAvatar(user));

							builder.appendField("User Blacklisted", user.getName(), true);
							builder.appendField("ID", user.getStringID(), true);
							builder.appendField("Blacklisted By", event.getAuthor().getName(), true);
							builder.appendField("ID", event.getAuthor().getStringID(), true);

							IChannel channel = Utility.getLogChannel(event.getGuild());
							if (channel == null) channel = event.getChannel();
							Message.sendEmbed(channel, "", builder.build(), false);
							RequestBuffer.request(() -> {
								event.getMessage().addReaction("✅");
							});
							if (DataBase.guildBooleanQuery(event.getGuild().getStringID(), "bpresentban"))
								Message.sendEmbed(channel, "", Utility.banUser(event.getGuild(), user, event.getClient().getOurUser(), "[Automated Blacklist Ban] " + Localisation.getMessage(event.getGuild().getStringID(), "quote-1")), false);
							Message.sendRawPM("[Automated Blacklist Ban] " + Localisation.getMessage(event.getGuild().getStringID(), "quote-1"), user);
						} else if (id != null) {
							DataBase.insertBlacklistEntry(event.getGuild().getStringID(), id, event.getAuthor().getStringID());

							EmbedBuilder builder = new EmbedBuilder();
							builder.withAuthorName("User " + id + " was blacklisted");
							builder.withColor(Color.RED);
							builder.withTimestamp(LocalDateTime.now());

							builder.appendField("User Blacklisted", "Unknown", true);
							builder.appendField("ID", id, true);
							builder.appendField("Blacklisted By", event.getAuthor().getName(), true);
							builder.appendField("ID", event.getAuthor().getStringID(), true);

							IChannel channel = Utility.getLogChannel(event.getGuild());
							if (channel == null) channel = event.getChannel();
							Message.sendEmbed(channel, "", builder.build(), false);
							RequestBuffer.request(() -> {
								event.getMessage().addReaction("✅");
							});
						} else {
							Message.sendMessageInChannel(event.getChannel(), "failed-blacklist");
							return;
						}
					} else {
						Message.sendMessageInChannel(event.getChannel(), help());
						return;
					}
					break;
				}
				case "remove": {
					if(args.length == 2) {
						IUser user = null;
						String id = null;
						try {
							user = event.getClient().getUserByID(Long.parseUnsignedLong(args[1]));
						} catch (NumberFormatException ignored) { }
						if (user == null && event.getMessage().getMentions().size() == 1)
							user = event.getMessage().getMentions().get(0);
						if (DataBase.queryIsBlacklisted(event.getGuild(), args[1])) {
							id = args[1];
						}

						if (user != null) {
							DataBase.removeBlacklistEntry(event.getGuild().getStringID(), user.getStringID());

							EmbedBuilder builder = new EmbedBuilder();
							builder.withAuthorIcon(Utility.getAvatar(user));
							builder.withAuthorName("User " + user.getName() + " was removed from the blacklist");
							builder.withColor(Color.GREEN);
							builder.withTimestamp(LocalDateTime.now());
							builder.withThumbnail(Utility.getAvatar(user));

							builder.appendField("User Removed", user.getName(), true);
							builder.appendField("ID", user.getStringID(), true);
							builder.appendField("Removed By", event.getAuthor().getName(), true);
							builder.appendField("ID", event.getAuthor().getStringID(), true);

							IChannel channel = Utility.getLogChannel(event.getGuild());
							if (channel == null) channel = event.getChannel();
							Message.sendEmbed(channel, "", builder.build(), false);
							RequestBuffer.request(() -> {
								event.getMessage().addReaction("✅");
							});
						} else if (id != null) {
							DataBase.removeBlacklistEntry(event.getGuild().getStringID(), id);

							EmbedBuilder builder = new EmbedBuilder();
							builder.withAuthorName("User " + id + " was removed from the blacklist");
							builder.withColor(Color.GREEN);
							builder.withTimestamp(LocalDateTime.now());

							builder.appendField("User Removed", "Unknown", true);
							builder.appendField("ID", id, true);
							builder.appendField("Removed By", event.getAuthor().getName(), true);
							builder.appendField("ID", event.getAuthor().getStringID(), true);

							IChannel channel = Utility.getLogChannel(event.getGuild());
							if (channel == null) channel = event.getChannel();
							Message.sendEmbed(channel, "", builder.build(), false);
							RequestBuffer.request(() -> {
								event.getMessage().addReaction("✅");
							});
						} else {
							Message.sendMessageInChannel(event.getChannel(), "failed-blacklist");
							return;
						}
					} else {
						Message.sendMessageInChannel(event.getChannel(), help());
						return;
					}
					break;
				}
				case "togglepban": {
					if (DataBase.guildBooleanQuery(event.getGuild().getStringID(), "bpresentban")) {
						DataBase.updateGuild(event.getGuild().getStringID(), "bpresentban", false);
						Message.sendMessageInChannel(event.getChannel(), "bpresentban-off");
					} else {
						DataBase.updateGuild(event.getGuild().getStringID(), "bpresentban", true);
						Message.sendMessageInChannel(event.getChannel(), "bpresentban-on");
					}
					break;
				}
				case "toggleignore": {
					if (DataBase.guildBooleanQuery(event.getGuild().getStringID(), "bignore")) {
						DataBase.updateGuild(event.getGuild().getStringID(), "bignore", false);
						Message.sendMessageInChannel(event.getChannel(), "bignore-off");
					} else {
						DataBase.updateGuild(event.getGuild().getStringID(), "bignore", true);
						Message.sendMessageInChannel(event.getChannel(), "bignore-on");
					}
					break;
				}
				case "help": {
					Message.sendMessageInChannel(event.getChannel(), help());
					break;
				}
				default: {
					Message.sendMessageInChannel(event.getChannel(), help());
				}
			}
		}
	}

	@Override
	public String help() {
		return "help-blacklist";
	}

	@Override
	public CommandType getType() {
		return CommandType.ADMIN;
	}
}
