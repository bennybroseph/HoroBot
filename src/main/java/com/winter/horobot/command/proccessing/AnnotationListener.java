/*
	HoroBot - An open-source Discord bot
	Copyright (C) 2017	WiNteR

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.winter.horobot.command.proccessing;

import com.winter.horobot.core.Main;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.profile.ProfileBuilder;
import com.winter.horobot.profile.ProfileTemplate;
import com.winter.horobot.util.Cooldowns;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.util.music.GuildMusicManager;
import com.winter.horobot.util.music.MusicUtils;
import org.apache.commons.lang3.text.WordUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageSendEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.shard.ReconnectSuccessEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AnnotationListener {

	private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 50, 1, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(1000),
			runnable -> new Thread(runnable, "Horo Message Handler"),
			new ThreadPoolExecutor.AbortPolicy());
	
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		event.getClient().changePlayingText(".horohelp | .horoinvite");
		Main.LOGGER.info("Ready event fired! Everything appears to be functional");
	}

	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		try {
			threadPool.submit(() -> {
				if (event.getClient().isReady()) {
					if (DataBase.guildQuery(event.getGuild().getStringID(), "id") == null) {
						DataBase.insertGuild(event.getGuild().getStringID());
						for (IChannel channel : event.getGuild().getChannels()) {
							if (channel.getModifiedPermissions(event.getGuild().getEveryoneRole()).contains(Permissions.SEND_MESSAGES)) {
								try {
									Message.sendRawMessageInChannel(channel,
											"This seems like a nice place for me to be, thanks for bringing me in :3\nType `.horohelp` to see what I can do for you!");
									break;
								} catch (MissingPermissionsException e) {
								}
							}
						}
					}
				}
				Main.LOGGER.info("Guild created with name" + event.getGuild().getName() + "!");
			});
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}
	}
	 
	@EventSubscriber
	public void onGuildLeaveEvent(GuildLeaveEvent event) {
		DataBase.deleteGuild(event.getGuild().getStringID());
	}
	
	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) {
		try {
			threadPool.submit(() -> {
				if(event.getClient().isReady()) {
					if (event.getMessage().getAuthor() != event.getClient().getOurUser() && !event.getMessage().getAuthor().isBot()) {
						if (!event.getChannel().isPrivate()) {
							//EmbedObject scanned = Utility.scanMessageAndAction(event.getGuild(), event.getMessage());
							//if (scanned != null) {
							//	Message.sendEmbed(Utility.getLogChannel(event.getGuild(), event.getChannel()), "", scanned, false);
							//}
							String prefix = DataBase.guildQuery(event.getGuild().getStringID(), "prefix");
							if (event.getMessage().getContent().startsWith(".horo")) {
								Main.handleCommand(Main.parser.parse(event.getMessage().getContent(), ".horo", event));
							} else if (prefix != null && event.getMessage().getContent().startsWith(prefix)) {
								Main.handleCommand(Main.parser.parse(event.getMessage().getContent(), prefix, event));
							}
						} else {
							if (event.getMessage().getContent().startsWith(".horo")) {
								Main.handleCommand(Main.parser.parse(event.getMessage().getContent(), ".horo", event));
							}
						}

						if (!Cooldowns.onCooldown("message-xp-" + event.getAuthor().getStringID(), event.getAuthor())) {
							DataBase.insertUser(event.getAuthor());
							DataBase.insertWolf(event.getAuthor());
							Cooldowns.putOnCooldown("message-xp-" + event.getAuthor().getStringID(), event.getAuthor(), 120000);
							DataBase.updateUser(event.getAuthor(), "xp", DataBase.queryUser(event.getAuthor()).getXp() + 30);
							ProfileTemplate template = DataBase.queryUser(event.getAuthor());
							if (template.getXp() >= template.getMaxXp()) {
								DataBase.updateUser(event.getAuthor(), "foxCoins", (template.getFoxCoins() + 100));
								DataBase.updateUser(event.getAuthor(), "level", (template.getLevel() + 1));
								DataBase.updateUser(event.getAuthor(), "xp", 0);
								DataBase.updateUser(event.getAuthor(), "maxXp", (template.getMaxXp() + 60));
								if (!event.getChannel().isPrivate() && DataBase.queryLvlUp(event.getGuild().getStringID())) {
									IChannel channel = null;
									for (IChannel temp : event.getGuild().getChannels()) {
										if (DataBase.channelQuery(temp.getStringID()).equals("log"))
											channel = temp;
									}
									if (channel == null) channel = event.getChannel();
									Message.sendFile(
											channel,
											"**" + event.getAuthor().getName() + " LEVELED UP!**\n" +
													"**+100 Coins** for leveling up!",
											"level-up.png",
											new ByteArrayInputStream(ProfileBuilder.generateLevelUp(event.getAuthor(), (template.getLevel() + 1))));
								}
							}
						}
						Utility.messagesReceived++;
					}
				}
			});
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}
	}
	
	@EventSubscriber
	public void onMessageSentEvent(MessageSendEvent event) {
		Utility.messagesSent++;
	}
	
	@EventSubscriber
	public void onReconnectedEvent(ReconnectSuccessEvent event) {
		Utility.reconnectedTimes++;
	}

	@EventSubscriber
	public void onVoiceLeaveEvent(UserVoiceChannelLeaveEvent event) {
		if(event.getClient().isReady()) {
			if (event.getUser() == event.getClient().getOurUser()) {
				GuildMusicManager manager = MusicUtils.getGuildAudioPlayer(event.getGuild());
				manager.player.stopTrack();
			}
		}
	}

	@EventSubscriber
	public void onUserJoinEvent(UserJoinEvent event) {
		try {
			threadPool.submit(() -> {
				if (event.getClient().isReady()) {
					IChannel channel = Utility.getLogChannel(event.getGuild());
					if (channel != null) {
						EmbedBuilder builder = new EmbedBuilder();
						builder.withAuthorIcon(Utility.getAvatar(event.getUser()));
						builder.withAuthorName(event.getUser().getName() + "#" + event.getUser().getDiscriminator());
						builder.withColor(Color.GREEN);
						builder.withThumbnail(Utility.getAvatar(event.getUser()));
						builder.withTimestamp(event.getJoinTime());

						builder.appendField("Name", event.getUser().getName(), true);
						builder.appendField("ID", event.getUser().getStringID(), true);
						builder.appendField("Bot", WordUtils.capitalize(Boolean.toString(event.getUser().isBot())), true);
						builder.appendField("Creation Date", "" + event.getUser().getCreationDate(), true);
						String welcome = DataBase.guildQuery(event.getGuild().getStringID(), "welcome");
						if (!"none".equals(welcome))
							builder.appendField("Welcome Message", Utility.formatWelcome(event.getGuild(), event.getUser(), welcome), false);
						Message.sendEmbed(channel, "", builder.build(), false);
					}
					String pm = DataBase.guildQuery(event.getGuild().getStringID(), "pm");
					if (!"none".equals(pm))
						event.getUser().getOrCreatePMChannel().sendMessage(Utility.formatWelcome(event.getGuild(), event.getUser(), pm));

					if (DataBase.queryIsBlacklisted(event.getGuild(), event.getUser())) {
						if (DataBase.guildBooleanQuery(event.getGuild().getStringID(), "bpresentban")) {
							Message.sendEmbed(channel, "", Utility.banUser(event.getGuild(), event.getUser(), event.getClient().getOurUser(), "[Automated Blacklist Ban] " + Localisation.getMessage(event.getGuild().getStringID(), "quote-1")), false);
						}
					}

					String roleID = DataBase.guildQuery(event.getGuild().getStringID(), "role");
					if (roleID != null) {
						IRole role = null;
						try {
							role = event.getGuild().getRoleByID(Long.parseUnsignedLong(roleID));
						} catch (NumberFormatException ignored) {
						}
						if (role != null) {
							try {
								final IRole temp = role;
								RequestBuffer.request(() -> {
									event.getUser().addRole(temp);
								});
							} catch (MissingPermissionsException e) {
								Message.sendPM(event.getGuild().getOwner(), "missing-role-perm", event.getUser().getName());
							}
						}
					}
				}
			});
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}
	}

	@EventSubscriber
	public void onUserLeaveEvent(UserLeaveEvent event) {
		try {
			threadPool.submit(() -> {
				if (event.getClient().isReady()) {
					for (IChannel channel : event.getGuild().getChannels()) {
						if (DataBase.channelQuery(channel.getStringID()).equals("log")) {
							EmbedBuilder builder = new EmbedBuilder();
							builder.withAuthorIcon(Utility.getAvatar(event.getUser()));
							builder.withAuthorName(event.getUser().getName() + "#" + event.getUser().getDiscriminator());
							builder.withColor(Color.RED);
							builder.withThumbnail(Utility.getAvatar(event.getUser()));
							builder.withTimestamp(LocalDateTime.now());

							builder.appendField("Name", event.getUser().getName(), true);
							builder.appendField("ID", event.getUser().getStringID(), true);
							builder.appendField("Bot", WordUtils.capitalize(Boolean.toString(event.getUser().isBot())), true);
							builder.appendField("Creation Date", "" + event.getUser().getCreationDate(), true);
							Message.sendEmbed(channel, "", builder.build(), false);
						}
					}
				}
			});
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}
	}
}