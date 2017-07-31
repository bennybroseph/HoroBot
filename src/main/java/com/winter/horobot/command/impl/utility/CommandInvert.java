package com.winter.horobot.command.impl.utility;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;

public class CommandInvert implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length > 0) {
			if (Utility.checkUserPermission(event.getGuild(), event.getClient().getOurUser(), Permissions.EMBED_LINKS)) {
				StringBuilder inverted = new StringBuilder();
				for (char c : raw.toCharArray()) {
					inverted.insert(0, c);
				}

				EmbedBuilder builder = new EmbedBuilder();
				builder.withColor(Color.CYAN);
				builder.withAuthorName("Requested by @" + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
				builder.withAuthorIcon(Utility.getAvatar(event.getAuthor()));
				builder.appendField("Here's the inverted text", inverted.toString(), false);

				Message.sendEmbed(event.getChannel(), "", builder.build(), false);
			} else {
				Message.sendMessageInChannel(event.getChannel(), "missing-embed-perm");
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "invert-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTILITY;
	}

	public void executed(boolean success, MessageReceivedEvent event) {

	}
}
