package com.winter.horobot.command.impl.fun;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.Random;

public class CommandHug implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(event.getMessage().getMentions().size() == 1) {
			if (Utility.checkUserPermission(event.getGuild(), event.getClient().getOurUser(), Permissions.EMBED_LINKS)) {
				Random rand = new Random();

				try {
					EmbedBuilder builder = new EmbedBuilder();

					String message = "";

					int result = rand.nextInt(6);
					switch (result) {
						case 0:
							message = "http://imgur.com/scNkWcj.gif";
							break;
						case 1:
							message = "http://imgur.com/KAxcvC4.gif";
							break;
						case 2:
							message = "http://imgur.com/OG6Xkgx.gif";
							break;
						case 3:
							message = "http://imgur.com/alBP2Hq.gif";
							break;
						case 4:
							message = "http://imgur.com/dMvK13X.gif";
							break;
						case 5:
							message = "http://imgur.com/X87fp5U.gif";
							break;
						case 6:
							message = "http://imgur.com/I3RsJk1.gif";
							break;
					}

					builder.withImage(message);
					builder.withColor(Color.CYAN);

					Message.sendEmbed(event.getChannel(), String.format(Localisation.getMessage(event.getGuild().getStringID(),
							"hug-from"),
							event.getMessage().getMentions().get(0).getName(),
							event.getAuthor().getName()
					), builder.build(), false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Message.sendMessageInChannel(event.getChannel(), "missing-embed-perm");
			}
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "hug-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.FUN;
	}

	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}