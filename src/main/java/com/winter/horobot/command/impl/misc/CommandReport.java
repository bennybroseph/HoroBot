package com.winter.horobot.command.impl.misc;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandReport implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if (args.length > 1) {
			if (event.getMessage().getMentions().size() == 1) {
				Message.sendEmbed(event.getChannel(), "", Utility.submitReport(event.getAuthor(), event.getMessage().getMentions().get(0), Arrays.stream(args).skip(1).collect(Collectors.joining(" "))), false);
			} else {
				Message.sendMessageInChannel(event.getChannel(), help());
			}
		} else {
			Message.sendMessageInChannel(event.getChannel(), help());
		}
	}

	@Override
	public String help() {
		return "report-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.MISC;
	}
}
