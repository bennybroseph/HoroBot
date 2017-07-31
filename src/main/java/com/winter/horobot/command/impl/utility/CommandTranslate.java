package com.winter.horobot.command.impl.utility;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.HTTPHandler;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

public class CommandTranslate implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length > 2) {
			if (Utility.checkUserPermission(event.getGuild(), event.getClient().getOurUser(), Permissions.EMBED_LINKS)) {
				String text = raw.replaceFirst(args[0] + " " + args[1], "");
				EmbedObject embed = null;
				try {
					embed = HTTPHandler.requestTranslation(args[0], args[1], text, event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), Utility.getAvatar(event.getAuthor()));
				} catch (Exception e) {
					Message.sendMessageInChannel(event.getChannel(), "html-error");
					return;
				}
				if(embed != null) {
					Message.sendEmbed(event.getChannel(), "", embed, false);
				} else {
					Message.sendMessageInChannel(event.getChannel(), "html-no-results");
				}
			} else {
				Message.sendMessageInChannel(event.getChannel(), "missing-embed-perm");
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "translate-help";
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