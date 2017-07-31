package com.winter.horobot.command.impl.fun;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.HTTPHandler;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;

public class CommandManga implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	@Override
	public CommandType getType() {
		return CommandType.FUN;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length > 0) {
			if (Utility.checkUserPermission(event.getGuild(), event.getClient().getOurUser(), Permissions.EMBED_LINKS)) {
				EmbedObject manga = null;
				try {
					manga = HTTPHandler.requestAnime(args, event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), Utility.getAvatar(event.getAuthor()), 1);
				} catch (Exception e) {
					Message.sendMessageInChannel(event.getChannel(), "html-error");
					return;
				}
				if (manga != null) {
					Message.sendEmbed(event.getChannel(),"", manga, false);
				} else {
					Message.reply("html-no-results", event.getMessage());
				}
			} else {
				Message.sendMessageInChannel(event.getChannel(), "missing-embed-perm");
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "manga-help";
	}

	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}