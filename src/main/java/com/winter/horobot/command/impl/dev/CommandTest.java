package com.winter.horobot.command.impl.dev;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.util.concurrent.TimeUnit;

public class CommandTest implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getStringID().equals("288996157202497536");
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		IMessage message = RequestBuffer.request(() -> { return event.getChannel().sendMessage("Pick a choice"); }).get();
		String choice = Utility.waitForReaction(message, event.getAuthor(), 30, TimeUnit.SECONDS, "\uD83D\uDD10");
		Message.sendRawMessageInChannel(event.getChannel(), "You chose " + choice);
	}

	@Override
	public String help() {
		return null;
	}

	@Override
	public CommandType getType() {
		return CommandType.DEVELOPER;
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent event) {

	}
}
