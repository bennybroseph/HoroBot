package com.winter.horobot.command.impl.fun;

import com.tsunderebug.iaatmt.jsonapis.KonaChan;
import com.winter.horobot.checks.ChannelChecks;
import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.command.Command;
import com.winter.horobot.data.Node;
import com.winter.horobot.util.MessageUtil;
import sx.blah.discord.handle.obj.Permissions;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

public class CommandKona extends Node<Command> {

	public CommandKona() {
		super(new Command(
				"kona",
				"kona-help",
				PermissionChecks.hasPermision(Permissions.SEND_MESSAGES),
				e -> {
					String[] tags = Arrays.copyOfRange(MessageUtil.argsArray(e.getMessage()), 2, MessageUtil.argsArray(e.getMessage()).length);
					KonaChan k = new KonaChan.Builder().withRating(KonaChan.KonaRating.SAFE).withTags(tags).build();
					try {
						URI u = k.randomURL();
						MessageUtil.sendImageEmbed(e.getChannel(), e.getAuthor(), u);
					} catch (IllegalArgumentException iae) {
						MessageUtil.sendMessage(e.getChannel(), "no-images", MessageUtil.args(e.getMessage()));
					}
					return true;
				}
		), Arrays.asList(
				new Node<>(new Command(
						"nsfw",
						"",
						PermissionChecks.hasPermision(Permissions.SEND_MESSAGES).and(ChannelChecks.isNSFW()),
						e -> {
							String[] tags = Arrays.copyOfRange(MessageUtil.argsArray(e.getMessage()), 2, MessageUtil.argsArray(e.getMessage()).length);
							KonaChan k = new KonaChan.Builder().withRating(KonaChan.KonaRating.EXPLICIT).withTags(tags).build();
							try {
								URI u = k.randomURL();
								MessageUtil.sendImageEmbed(e.getChannel(), e.getAuthor(), u);
							} catch (IllegalArgumentException iae) {
								MessageUtil.sendMessage(e.getChannel(), "no-images", MessageUtil.args(e.getMessage()));
							}
							return true;
						},
						Collections.singleton("explicit")
				), Collections.emptyList())
		));
	}
}
