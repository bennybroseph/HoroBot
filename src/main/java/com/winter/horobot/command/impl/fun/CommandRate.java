package com.winter.horobot.command.impl.fun;

import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.command.proccessing.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.Random;

public class CommandRate implements Command {

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if(args.length > 0) {
			Random rand = new Random();
			int rate = rand.nextInt(100);

			Message.sendMessageInChannel(event.getChannel(), "rate", raw, rate + "/100");
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "rate-help";
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
