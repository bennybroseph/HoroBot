package com.winter.horobot.util;

import com.winter.horobot.data.locale.Localisation;
import com.winter.horobot.exceptions.ErrorHandler;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageUtil {

	// TODO: Document these 2 methods, I have no idea what they do TsundereBug!!!!

	public static String[] argsArray(IMessage m) {
		Optional<String> o = GuildUtil.getPrefixes(m.getGuild()).stream().filter(m.getContent()::startsWith).findAny();
		return o.map(s -> m.getContent().substring(s.length())).orElseGet(m::getContent).split("\\s+");
	}

	public static String args(IMessage m) {
		return Arrays.stream(argsArray(m)).collect(Collectors.joining(" "));
	}

	/**
	 * Send a message in a channel, params are what the %s in the message will be replaced with
	 * @param channel The channel to send the message in
	 * @param messageKey The localisation key for the message
	 * @param params The replacements for %s in the message
	 */
	public static void sendMessage(IChannel channel, String messageKey, Object... params) {
		RequestBuffer.request(() -> channel.sendMessage(Localisation.getMessage(channel.getGuild(), messageKey, params)));
	}

	/**
	 * Send an embed in a channel
	 * @param channel The channel to send the message in
	 * @param embed The embed object to send
	 */
	public static void sendMessage(IChannel channel, EmbedObject embed) {
		RequestBuffer.request(() -> channel.sendMessage("", embed));
	}

	/**
	 * Send an embed in a channel with a message
	 * @param channel The channel to send the message in
	 * @param embed The embed object to send
	 * @param messageKey The localisation key for the message
	 * @param params The replacements for %s in the message
	 */
	public static void sendMessage(IChannel channel, EmbedObject embed, String messageKey, Object... params) {
		RequestBuffer.request(() -> channel.sendMessage(Localisation.getMessage(channel.getGuild(), messageKey, params), embed));
	}

	/**
	 * Send an image embed in a channel
	 * @param channel The channel to send the message in
	 * @param requested The user who requested the image
	 * @param uri The URI to get the image from
	 */
	public static void sendImageEmbed(IChannel channel, IUser requested, URI uri) {
		RequestBuffer.request(() -> {
			EmbedBuilder b = new EmbedBuilder();
			b.withAuthorIcon(ImageUtil.getAvatar(requested));
			b.withAuthorName("Requested by " + requested.getDisplayName(channel.getGuild()));
			b.withAuthorUrl(uri.toString());
			try {
				BufferedImage i = ImageUtil.imageFromURL(uri.toURL());
				b.withColor(ImageUtil.averageColor(i));
				b.withImage(uri.toString());
				channel.sendMessage(b.build());
			} catch (IOException e) {
				ErrorHandler.log(e, e.getMessage());
			}
		});
	}

}
