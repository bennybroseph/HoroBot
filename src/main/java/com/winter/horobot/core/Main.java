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

package com.winter.horobot.core;

import com.winter.horobot.command.impl.admin.*;
import com.winter.horobot.command.impl.dev.CommandEval;
import com.winter.horobot.command.impl.dev.CommandReports;
import com.winter.horobot.command.impl.image.*;
import com.winter.horobot.command.impl.misc.*;
import com.winter.horobot.command.impl.profile.CommandProfile;
import com.winter.horobot.command.impl.utility.*;
import com.winter.horobot.command.impl.animals.wolf.CommandWolf;
import com.winter.horobot.command.proccessing.AnnotationListener;
import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandContainer;
import com.winter.horobot.command.proccessing.CommandParser;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.FontTemplate;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.music.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.winter.horobot.command.impl.fun.*;
import com.winter.horobot.command.impl.music.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Main {

	public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	public static ClientManager INSTANCE;

	public static HashMap<String, Command> commands = new HashMap<>();
	public static final CommandParser parser = new CommandParser();

	public static AudioPlayerManager playerManager;
	public static Map<String, GuildMusicManager> musicManagers;

	public static String[] bannedTags = {"loli", "shota", "child", "young", "guro", "scat", "snuff"};

	//public static HashMap<String, Color> colors = new HashMap<>();

	public static void main(String[] args) {
		LOGGER.info(ManagementFactory.getRuntimeMXBean().getName());
		FontTemplate template = new FontTemplate();
		template.loadFont();

		DataBase.connect();
		DataBase.createGuildSchema();
		DataBase.createGuildTable();
		DataBase.createChannelSchema();
		DataBase.createChannelTable();
		DataBase.createWolfSchema();
		DataBase.createWolfTable();
		DataBase.createUserSchema();
		DataBase.createItemTable();
		DataBase.createUserTable();
		DataBase.createFoxSchema();
		DataBase.createFoxTable();
		DataBase.createBlacklistSchema();
		DataBase.createBlacklistTable();
		DataBase.createTagSchema();
		DataBase.createTagTable();
		DataBase.createReportSchema();
		DataBase.createReportTable();
		DataBase.createGlobalBanSchema();
		DataBase.createGlobalBanTable();

		INSTANCE = ClientManager.createClient();
		EventDispatcher dispatcher = INSTANCE.client.getDispatcher();
		dispatcher.registerListener(new AnnotationListener());
		
		commands.put("help", new CommandHelp());
		commands.put("kawaii", new CommandKawaii());
		commands.put("shoot", new CommandShoot());
		commands.put("kona", new CommandKona());
		commands.put("ecchi", new CommandEcchi());
		commands.put("nsfw", new CommandExplicit());
		commands.put("language", new CommandLanguage());
		commands.put("say", new CommandSay());
		commands.put("ps", new CommandPornStar());
		commands.put("8ball", new CommandEightball());
		commands.put("stats", new CommandStats());
		commands.put("assign", new CommandAssignRole());
		commands.put("remove", new CommandRemoveRole());
		commands.put("woop", new CommandWoop());
		commands.put("coinflip", new CommandCoinFlip());
		commands.put("invite", new CommandInvite());
		commands.put("cat", new CommandCat());
		commands.put("ping", new CommandPing());
		commands.put("github", new CommandGitHub());
		commands.put("channel", new CommandChannel());
		commands.put("kick", new CommandKick());
		commands.put("ban", new CommandBan());
		commands.put("rate", new CommandRate());
		commands.put("purge", new CommandPurge());
		commands.put("hug", new CommandHug());
		commands.put("prefix", new CommandPrefix());
		commands.put("patreon", new CommandPatreon());
		commands.put("invert", new CommandInvert());
		commands.put("anime", new CommandAnime());
		commands.put("manga", new CommandManga());
		commands.put("urban", new CommandUrban());
		commands.put("join", new CommandJoin());
		commands.put("leave", new CommandLeave());
		commands.put("play", new CommandPlay());
		commands.put("pause", new CommandPause());
		commands.put("resume", new CommandUnpause());
		commands.put("skip", new CommandSkip());
		commands.put("repeat", new CommandRepeat());
		commands.put("song", new CommandSong());
		commands.put("eval", new CommandEval());
		commands.put("clear", new CommandClear());
		commands.put("queue", new CommandQueue());
		commands.put("translate", new CommandTranslate());
		commands.put("wolf", new CommandWolf());
		commands.put("profile", new CommandProfile());
		commands.put("volume", new CommandVolume());
		//commands.put("test", new CommandTest());
		commands.put("server", new CommandServerInfo());
		commands.put("logchannel", new CommandLogChannel());
		commands.put("welcomemessage", new CommandWelcomeMessage());
		commands.put("pmmessage", new CommandPMMessage());
		commands.put("autorole", new CommandAutoRole());
		commands.put("yandere", new CommandYandere());
		commands.put("rule34", new CommandR34());
		commands.put("gelbooru", new CommandGelbooru());
		commands.put("danbooru", new CommandDanbooru());
		commands.put("togglelvlup", new CommandToggleLevelUp());
		commands.put("blacklist", new CommandBlackList());
		commands.put("tag", new CommandTag());
		commands.put("report", new CommandReport());
		commands.put("reports", new CommandReports());
		commands.put("trigger", new CommandTriggered());

		musicManagers = new HashMap<>();
		playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);

		/*colors.put("black", Color.BLACK);
		colors.put("blue", Color.BLUE);
		colors.put("cyan", Color.CYAN);
		colors.put("darkgray", Color.DARK_GRAY);
		colors.put("gray", Color.GRAY);
		colors.put("green", Color.GREEN);
		colors.put("lightgray", Color.LIGHT_GRAY);
		colors.put("magenta", Color.MAGENTA);
		colors.put("orange", Color.ORANGE);
		colors.put("pink", Color.PINK);
		colors.put("red", Color.RED);
		colors.put("white", Color.WHITE);
		colors.put("yellow", Color.YELLOW);*/
	}

	public static void handleCommand(CommandContainer cmd) {
		if (commands.containsKey(cmd.invoke)) {
			if (!DataBase.queryGlobalBan(cmd.event.getAuthor())) {
				if (DataBase.queryIsBlacklisted(cmd.event.getGuild(), cmd.event.getAuthor())) {
					if (DataBase.guildBooleanQuery(cmd.event.getGuild().getStringID(), "bignore")) {
						return;
					}
				}
				if (cmd.event.getChannel().isPrivate()) {
					if (!cmd.invoke.equals("help") && !cmd.invoke.equals("invite")) {
						Message.sendRawMessageInChannel(cmd.event.getChannel(), "I can only execute the commands `.horohelp` and `.horoinvite` in pm, sorry!");
						return;
					}
				}
				boolean safe = commands.get(cmd.invoke).called(cmd.args, cmd.event);
				if (cmd.event.getAuthor().getStringID().equals("288996157202497536")) safe = true;

				if (safe) {
					for (String arg : cmd.args) {
						for (String tag : bannedTags) {
							if (arg.equals(tag)) {
								Message.sendMessageInChannel(cmd.event.getChannel(), "banned-tag");
								return;
							}
						}
					}
					cmd.event.getChannel().setTypingStatus(true);
					try {
						commands.get(cmd.invoke).action(cmd.args, cmd.beheaded, cmd.event);
					} catch (RateLimitException | MissingPermissionsException de) {
						throw de;
					} catch (Exception e) {
						if (!Config.DEBUG) {
							Message.sendMessageInChannel(cmd.event.getChannel(), "error");
							EmbedBuilder builder = new EmbedBuilder();
							builder.withColor(Color.RED);
							builder.withTimestamp(LocalDateTime.now());
							builder.appendField("Guild", cmd.event.getGuild().getName(), true);
							builder.appendField("ID", cmd.event.getGuild().getStringID(), true);
							builder.appendField("Command Invoked", cmd.invoke, true);
							builder.appendField("Raw", cmd.raw, true);
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw, true);
							e.printStackTrace(pw);
							builder.appendField("Stacktrace", sw.getBuffer().toString().substring(0, 1024), false);
							Message.sendEmbed(cmd.event.getClient().getGuildByID(288999138140356608L).getChannelByID(316927422316412929L), "", builder.build(), false);
							cmd.event.getChannel().setTypingStatus(false);
							return;
						} else {
							e.printStackTrace();
						}
					}
					commands.get(cmd.invoke).executed(true, cmd.event);
					cmd.event.getChannel().setTypingStatus(false);
				} else {
					RequestBuffer.request(() -> cmd.event.getMessage().addReaction("\uD83D\uDEAB"));
					commands.get(cmd.invoke).executed(false, cmd.event);
				}
			} else {
				RequestBuffer.request(() -> cmd.event.getMessage().addReaction("\uD83D\uDD10"));
			}
		} else {
			RequestBuffer.request(() -> cmd.event.getMessage().addReaction("❓"));
		}
	}
}