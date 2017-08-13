package com.winter.horobot.command.impl.fun;

import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.command.Command;
import com.winter.horobot.data.Node;
import com.winter.horobot.data.locale.Localisation;
import com.winter.horobot.util.ColorUtil;
import com.winter.horobot.util.MessageUtil;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.Collections;

public class CommandHue extends Node<Command> {

	public CommandHue() {
		super(new Command(
				"hue",
				PermissionChecks.hasPermision(Permissions.SEND_MESSAGES),
				e -> {
					float lower = Float.parseFloat(MessageUtil.argsArray(e.getMessage())[1]);
					float upper = Float.parseFloat(MessageUtil.argsArray(e.getMessage())[2]);
					Color c = ColorUtil.withinTwoHues(lower, upper);
					EmbedBuilder eb = new EmbedBuilder();
					eb.appendDescription(String.format(Localisation.of(e.getGuild(), "get"), "#" + Integer.toHexString(c.getRGB()).substring(2, 8)));
					eb.withColor(c);
					e.getChannel().sendMessage(eb.build());
					return true;
				}
		), Collections.emptyList());
	}
}
