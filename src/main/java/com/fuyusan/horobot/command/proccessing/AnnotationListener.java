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

package com.fuyusan.horobot.command.proccessing;

import com.fuyusan.horobot.core.Main;
import com.fuyusan.horobot.database.DataBase;
import com.fuyusan.horobot.profile.ProfileBuilder;
import com.fuyusan.horobot.profile.ProfileTemplate;
import com.fuyusan.horobot.util.Cooldowns;
import com.fuyusan.horobot.util.Message;
import com.fuyusan.horobot.util.Utility;
import com.fuyusan.horobot.util.music.MusicUtils;
import com.sun.media.jfxmedia.logging.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageSendEvent;
import sx.blah.discord.handle.impl.events.shard.ReconnectSuccessEvent;

public class AnnotationListener {
	
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		try {
			event.getClient().changePlayingText(".horohelp | .horoinvite");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		if (event.getClient().isReady()) {
			if(DataBase.guildQuery(event.getGuild().getID(), "id") == null) {
				DataBase.insertGuild(event.getGuild().getID());
				Message.sendRawMessageInChannel(event.getGuild().getChannels().get(0),
						"This seems like a nice place for me to be, thanks for bringing me in :3\nType `.horohelp` to see what I can do for you!");
				Main.LOGGER.info(String.format("New guild created:\n" +
								"Name: %s\n" +
								"ID: %s\n" +
								"Owner: %s\n" +
								"Owner ID: %s\n" +
								"Users: %s\n",
						event.getGuild().getName(),
						event.getGuild().getID(),
						event.getGuild().getOwner().getName(),
						event.getGuild().getOwner().getID(),
						event.getGuild().getUsers().size()));
			}
		}
	}
	
	@EventSubscriber
	public void onGuildLeaveEvent(GuildLeaveEvent event) {
		MusicUtils.getGuildAudioPlayer(event.getGuild()).player.stopTrack();
		DataBase.deleteGuild(event.getGuild().getID());
		Main.LOGGER.info(String.format("Guild deleted:\n" +
				"Name: %s\n" +
				"ID: %s\n" +
				"Owner: %s\n" +
				"Owner ID: %s\n" +
				"Users: %s\n",
				event.getGuild().getName(),
				event.getGuild().getID(),
				event.getGuild().getOwner().getName(),
				event.getGuild().getOwner().getID(),
				event.getGuild().getUsers().size()));
	}
	
	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) {
		if(event.getMessage().getAuthor() != event.getClient().getOurUser()) {
			if (!Cooldowns.onCooldown("message-xp", 120000, event.getAuthor())) {
				Cooldowns.putOnCooldown("message-xp", event.getAuthor());
				DataBase.updateUser(event.getAuthor(), "xp", DataBase.queryUser(event.getAuthor()).getXp() + 30);
				ProfileTemplate template = DataBase.queryUser(event.getAuthor());
				if (template.getXp() >= template.getMaxXp()) {
					DataBase.updateUser(event.getAuthor(), "level", (template.getLevel() + 1));
					Message.sendFile(
							event.getChannel(),
							"**LEVEL UP!**",
							ProfileBuilder.generateLevelUp(event.getAuthor(), (template.getLevel() + 1));
				}
			}
			String prefix = DataBase.guildQuery(event.getGuild().getID(), "prefix");
			if(event.getMessage().getContent().startsWith(".horo")) {
				Main.handleCommand(Main.parser.parse(event.getMessage().getContent(), ".horo", event));
			} else if(event.getMessage().getContent().startsWith(prefix)) {
				Main.handleCommand(Main.parser.parse(event.getMessage().getContent(), prefix, event));
			}
		}
		Utility.messagesReceived++;
	}
	
	@EventSubscriber
	public void onMessageSentEvent(MessageSendEvent event) {
		Utility.messagesSent++;
	}
	
	@EventSubscriber
	public void onReconnectedEvent(ReconnectSuccessEvent event) {
		Utility.reconnectedTimes++;
	}
}