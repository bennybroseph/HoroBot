package com.winter.horobot.command.impl.utility;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;

public class CommandServerInfo implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.withThumbnail(event.getGuild().getIconURL());
		builder.withAuthorName("Requested by @" + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator());
		builder.withAuthorIcon(Utility.getAvatar(event.getAuthor()));
		builder.withColor(Color.CYAN);
		builder.appendField("Name", event.getGuild().getName(), true);
		builder.appendField("ID", event.getGuild().getStringID(), true);
		builder.appendField("Owner", event.getGuild().getOwner().getName(), true);
		builder.appendField("Region", event.getGuild().getRegion().getName(), true);
		builder.appendField("Channels", String.valueOf(event.getGuild().getChannels().size()), true);
		builder.appendField("Users", String.valueOf(event.getGuild().getUsers().size()), true);
		ArrayList<String> roles = new ArrayList<>();
		for(IRole role : event.getGuild().getRoles()) roles.add(role.getName());
		builder.appendField("Roles", Utility.listAsString(roles), false);

		Message.sendEmbed(event.getChannel(), Localisation.getMessage(event.getGuild().getStringID(), "server-info"), builder.build(), false);
	}

	@Override
	public String help() {
		return "help-server";
	}

	@Override
	public CommandType getType() {
		return CommandType.UTILITY;
	}
}