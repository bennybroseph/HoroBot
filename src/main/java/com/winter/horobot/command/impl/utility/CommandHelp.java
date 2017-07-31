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

package com.winter.horobot.command.impl.utility;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.core.Main;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import org.apache.commons.lang3.text.WordUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.stream.Collectors;

public class CommandHelp implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if (args.length == 0) {
			for(int i = 0; i < CommandType.getTypeCount(); i++) {
				CommandType c = CommandType.getTypes()[i];

				if (c.getCommands(c).size() == 0) continue;

				String help;
				if(!event.getChannel().isPrivate()) {
					help = c.getCommands(c)
							.stream()
							.map(command -> "**.horo" + command + "** - " + Localisation.getMessage(event.getGuild().getStringID(), Main.commands.get(command).help()) + "\n")
							.collect(Collectors.joining(""));
				} else {
					help = c.getCommands(c)
							.stream()
							.map(command -> "**.horo" + command + "** - " + Localisation.getPMMessage(Main.commands.get(command).help()) + "\n")
							.collect(Collectors.joining(""));
				}

				int arrayLength = (int) Math.ceil(((help.length() / (double) 1024)));
				String result[] = new String[arrayLength];

				int j = 0;
				int lastIndex = result.length - 1;
				for (int x = 0; x < lastIndex; x++) {
					result[x] = help.substring(j, j + 1024);
					j += 1024;
				}
				result[lastIndex] = help.substring(j);

				EmbedBuilder builder = new EmbedBuilder();
				builder.withColor(Color.CYAN);
				for (String part : result) {
					builder.appendField(WordUtils.capitalizeFully(c.toString()), part, false);
				}
				Message.sendPMEmbed(event.getAuthor(), "", builder.build(), false);
			}

			Message.sendRawPM("Hey there!\n\n" +
					"I know everyone hates these kinds of messages and so do I so I'll keep it short,\n" +
					"HoroBot is a project which I've been working on for the past month for fun but I am in desperate need of funds to keep her running.\n" +
					"So should you even have 1 dollar to spare, please head over to https://www.patreon.com/HoroBot and help me out, or for a one-time donation go here; https://paypal.me/HoroBot \n\n" +
					"Thanks a lot for reading and helping me out!", event.getAuthor());
			if(!event.getMessage().getChannel().isPrivate()) event.getMessage().delete();
		} else if(args.length == 1) {
			if(!event.getMessage().getChannel().isPrivate()) {
				String guildID = event.getMessage().getGuild().getStringID();
				if (Main.commands.containsKey(args[0])) {
					Message.replyRaw(Localisation.getMessage(guildID, Main.commands.get(args[0]).help()), event.getMessage());
				} else {
					Message.replyRaw("`" + args[0] + "` " + Localisation.getMessage(guildID, "no-command") + "\n", event.getMessage());
				}
			} else {
				Message.reply("private-channel", event.getMessage());
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "help-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTILITY;
	}

	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}