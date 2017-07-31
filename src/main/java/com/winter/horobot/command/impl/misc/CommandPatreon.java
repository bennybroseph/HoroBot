package com.winter.horobot.command.impl.misc;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

public class CommandPatreon implements Command {

	@Override
	public CommandType getType() {
		return CommandType.MISC;
	}

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length == 0) {
			if (Utility.checkUserPermission(event.getGuild(), event.getClient().getOurUser(), Permissions.EMBED_LINKS)) {
				EmbedBuilder builder = new EmbedBuilder();
				builder.withFooterIcon("https://cdn.discordapp.com/avatars/288996157202497536/292e2ad51032535171b8e852444fb958.webp?size=256");
				builder.withFooterText("Winter#9815 288996157202497536");
				builder.withColor(Color.CYAN);
				builder.appendField("HoroBot Patreon", "https://www.patreon.com/HoroBot", false);

				Message.sendEmbed(event.getChannel(), "", builder.build(), false);
			} else {
				Message.sendMessageInChannel(event.getChannel(), "missing-embed-perm");
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "patreon-help";
	}

	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}