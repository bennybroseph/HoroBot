package com.winter.horobot.exceptions;

import com.winter.horobot.Main;
import com.winter.horobot.data.locale.Localisation;
import com.winter.horobot.util.ColorUtil;
import com.winter.horobot.util.MessageUtil;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Arrays;

public class ErrorHandler {

	/**
	 * DO NOT USE THIS METHOD IF THE ERROR OCCURRED WHILE HANDLING A COMMAND!!!!!
	 * @param t The throwable that caused the error
	 * @param meta The meta message to append
	 */
	public static void log(Throwable t, String meta) {
		MessageUtil.sendMessage(Main.getClient().getChannelByID(Long.parseLong(Main.config.get(Main.ConfigValue.ERROR_CHANNEL))),
				new EmbedBuilder()
						.setLenient(false)
						.withColor(ColorUtil.withinTwoHues(0.7f, 1.2f))
						.withTitle("An error has occurred! " + meta)
						.appendDescription("```\n")
						.appendDescription(t.toString())
						.appendDescription(Arrays.toString(t.getStackTrace()))
						.appendDescription("\n```")
						.build());
	}

	/**
	 * Log an error to the error channel and also notify the user
	 * @param t The throwable that caused the error
	 * @param channel The channel to notify the user in
	 */
	public static void log(Throwable t, IChannel channel) {
		if (!(t instanceof UpdateFailedException)) {
			MessageUtil.sendMessage(Main.getClient().getChannelByID(Long.parseLong(Main.config.get(Main.ConfigValue.ERROR_CHANNEL))),
					new EmbedBuilder()
							.setLenient(false)
							.withColor(ColorUtil.withinTwoHues(0.7f, 1.2f))
							.withTitle("An error has occurred!")
							.appendDescription("```\n")
							.appendDescription(t.toString())
							.appendDescription(Arrays.toString(t.getStackTrace()))
							.appendDescription("\n```")
							.build());
		}
		MessageUtil.sendMessage(channel,
				new EmbedBuilder()
						.setLenient(false)
						.withColor(ColorUtil.withinTwoHues(0.7f, 1.2f))
						.withTitle(Localisation.getMessage(channel.getGuild(), "error"))
						.withDescription(t.getMessage())
						.build());
	}
}
