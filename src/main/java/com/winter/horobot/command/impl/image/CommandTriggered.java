package com.winter.horobot.command.impl.image;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CommandTriggered implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		AnimatedGifEncoder age = new AnimatedGifEncoder();
		if(args.length == 1) {
			IUser u = event.getMessage().getMentions().get(0);
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			age.start(o);
			age.setDelay(30);
			age.setRepeat(0);
			try {
				List<BufferedImage> avatarFrames = null;
				try {
					URLConnection conn = new URI(u.getAvatarURL().substring(0, u.getAvatarURL().lastIndexOf('.')) + ".gif").toURL().openConnection();
					conn.addRequestProperty("User-Agent", "Mozzila/5.0");
					avatarFrames = Utility.getGifFramesFromStream(conn.getInputStream());
				} catch(Exception e) {
					URLConnection conn = new URI(u.getAvatarURL().substring(0, u.getAvatarURL().lastIndexOf('.')) + ".png").toURL().openConnection();
					conn.addRequestProperty("User-Agent", "Mozzila/5.0");
					avatarFrames = Collections.singletonList(ImageIO.read(conn.getInputStream()));
				}
				List<BufferedImage> triggeredFrames = Utility.getGifFramesFromStream(new URI("http://i.imgur.com/9WSux7S.gif").toURL().openStream());
				for(int i = 0; i < 200; i++) {
					BufferedImage avatarFrame = Utility.copyImage(avatarFrames.get(i % avatarFrames.size()));
					BufferedImage triggeredFrame = triggeredFrames.get(i % triggeredFrames.size());
					Graphics2D avatarGraphics = avatarFrame.createGraphics();
					avatarGraphics.setColor(new Color(1.0f, 0.0f, 0.0f,i * 0.005f));
					avatarGraphics.fillRect(0, 0, avatarFrame.getWidth(), avatarFrame.getHeight());
					avatarGraphics.setColor(Color.WHITE);
					avatarGraphics.drawImage(triggeredFrame, 0, 0, avatarFrame.getWidth(), avatarFrame.getHeight(), null);
					age.addFrame(avatarFrame);
				}
				age.finish();
				Message.sendFile(event.getChannel(), "", "triggered.gif", new ByteArrayInputStream(o.toByteArray()));
			} catch (IOException e) {
				e.printStackTrace(); // TODO correctly handle exceptions
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else if(args.length == 0) {
			IUser u = event.getAuthor();
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			age.start(o);
			age.setDelay(30);
			age.setRepeat(0);
			try {
				List<BufferedImage> avatarFrames = null;
				try {
					URLConnection conn = new URI(u.getAvatarURL().substring(0, u.getAvatarURL().lastIndexOf('.')) + ".gif").toURL().openConnection();
					conn.addRequestProperty("User-Agent", "Mozzila/5.0");
					avatarFrames = Utility.getGifFramesFromStream(conn.getInputStream());
				} catch(Exception e) {
					URLConnection conn = new URI(u.getAvatarURL().substring(0, u.getAvatarURL().lastIndexOf('.')) + ".png").toURL().openConnection();
					conn.addRequestProperty("User-Agent", "Mozzila/5.0");
					avatarFrames = Collections.singletonList(ImageIO.read(conn.getInputStream()));
				}
				List<BufferedImage> triggeredFrames = Utility.getGifFramesFromStream(new URI("http://i.imgur.com/9WSux7S.gif").toURL().openStream());
				for(int i = 0; i < 200; i++) {
					BufferedImage avatarFrame = Utility.copyImage(avatarFrames.get(i % avatarFrames.size()));
					BufferedImage triggeredFrame = triggeredFrames.get(i % triggeredFrames.size());
					Graphics2D avatarGraphics = avatarFrame.createGraphics();
					avatarGraphics.setColor(new Color(1.0f, 0.0f, 0.0f,i * 0.005f));
					avatarGraphics.fillRect(0, 0, avatarFrame.getWidth(), avatarFrame.getHeight());
					avatarGraphics.setColor(Color.WHITE);
					avatarGraphics.drawImage(triggeredFrame, 0, 0, avatarFrame.getWidth(), avatarFrame.getHeight(), null);
					age.addFrame(avatarFrame);
				}
				age.finish();
				Message.sendFile(event.getChannel(), "", "triggered.gif", new ByteArrayInputStream(o.toByteArray()));
			} catch (IOException e) {
				e.printStackTrace(); // TODO correctly handle exceptions
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			Message.sendMessageInChannel(event.getChannel(), help());
		}
	}

	@Override
	public String help() {
		return "triggered-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.IMAGE;
	}

}
