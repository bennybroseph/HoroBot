/*
	HoroBot - An open-source Discord bot
	Copyright (C) 2017	WiNteR

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.winter.horobot.util;

import com.madgag.gif.fmsware.GifDecoder;
import com.winter.horobot.animals.Inventory;
import com.winter.horobot.animals.Item;
import com.winter.horobot.animals.wolf.WolfCosmetics;
import com.winter.horobot.core.Config;
import com.winter.horobot.core.Main;
import com.winter.horobot.database.DataBase;
import org.apache.commons.lang3.text.WordUtils;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Utility {

	public static String Discord4JVersion = Discord4J.VERSION;
	public static long commandsExecuted = 0;
	public static long totalCommands = 0;
	public static String runStart;
	public static String runTime;
	public static long reconnectedTimes = 0;
	public static String memory;
	public static long messagesReceived = 0;
	public static long messagesSent = 0;
	private static String[] defaults = {
			"6debd47ed13483642cf09e832ed0bc1b",
			"322c936a8c8be1b803cd94861bdfa868",
			"dd4dbc0016779df1378e7812eabaa04d",
			"0e291f67c9274a1abdddeb3fd919cbaa",
			"1cbd08c76f8af6dddce02c5138971129"
	};

	public static IUser getFirstUser(IGuild guild, String search) {
		IUser user = null;
		try {
			user = guild.getUserByID(Long.parseUnsignedLong(search));
		} catch (NumberFormatException ignored) { }

		if (user == null) {
			List<IUser> users = guild.getUsersByName(search);
			if (users.size() == 1) {
				user = users.get(0);
			}
		}
		return user;
	}

	public static IRole getFirstRole(IGuild guild, String search) {
		IRole role = null;
		try {
			role = guild.getRoleByID(Long.parseUnsignedLong(search));
		} catch (NumberFormatException ignored) { }

		if (role == null && guild.getRolesByName(search).size() == 1) {
			role = guild.getRolesByName(search).get(0);
		}
		return role;
	}

	public static String waitForInput(IChannel channel, IUser user, long timeout, TimeUnit timeUnit) {
		String obj = null;
		try {
			obj = Main.INSTANCE.client.getDispatcher().waitFor(
					(MessageReceivedEvent e) ->
							e.getChannel().equals(channel) &&
									e.getAuthor().equals(user),
					timeout, timeUnit)
					.getMessage()
					.getContent();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NullPointerException ignored) { }
		return obj;
	}

	public static String waitForChoice(IChannel channel, IUser user, long timeout, TimeUnit timeUnit, String... choices) {
		String obj = null;
		try {
			obj = Main.INSTANCE.client.getDispatcher().waitFor(
					(MessageReceivedEvent e) ->
							e.getChannel().equals(channel) &&
							e.getAuthor().equals(user) &&
							Arrays.asList(choices).contains(e.getMessage().getContent()),
					timeout, timeUnit)
					.getMessage()
					.getContent();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NullPointerException ignored) { }
		return obj;
	}

	public static String waitForReaction(IMessage message, IUser user, long timeout, TimeUnit timeUnit, String... choises) {
		String obj = null;
		for (String reaction : choises) {
			RequestBuffer.request(() -> message.addReaction(reaction)).get();
		}

		try {
			obj = Main.INSTANCE.client.getDispatcher().waitFor(
					(ReactionAddEvent e) ->
							e.getMessage().equals(message) &&
							e.getUser().equals(user) &&
							Arrays.asList(choises).contains(e.getReaction().toString()),
					timeout, timeUnit)
					.getReaction()
					.toString();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NullPointerException ignored) { }
		return obj;
	}

	public static EmbedObject submitReport(IUser author, IUser target, String reason) {
		if (reason.length() > 1024) reason = reason.substring(0, 1024);

		EmbedBuilder builder = new EmbedBuilder();
		builder.withColor(Color.YELLOW);
		builder.withTimestamp(LocalDateTime.now());
		builder.withThumbnail(Utility.getAvatar(target));
		builder.withAuthorIcon(Utility.getAvatar(target));
		builder.withAuthorName(target.getName());
		String reportID = DataBase.submitReport(author, target, reason);
		builder.withTitle("Report submitted for " + target.getName() + " with report id `" + reportID + "`");
		builder.appendField("Target", target.getName(), true);
		builder.appendField("ID", target.getStringID(), true);
		builder.appendField("Submitter", author.getName(), true);
		builder.appendField("ID", author.getStringID(), true);
		builder.appendField("Evidence", reason, false);

		IMessage message = Message.sendEmbedGet(Main.INSTANCE.client.getGuildByID(288999138140356608L).getChannelByID(318002471878393856L), "", builder.build(), false);
		DataBase.updateReport(reportID, "messageID", message.getStringID());

		return builder.build();
	}

	public static Inventory assembleInventory(IUser user) {
		List<Item> backgrounds = new ArrayList<>();
		List<Item> hats = new ArrayList<>();
		List<Item> bodies = new ArrayList<>();
		List<Item> paws = new ArrayList<>();
		List<Item> tails = new ArrayList<>();
		List<Item> shirts = new ArrayList<>();
		List<Item> noses = new ArrayList<>();
		List<Item> eyes = new ArrayList<>();
		List<Item> neck = new ArrayList<>();

		List<Item> items = DataBase.queryItems(user);
		if (items == null) return null;
		for (Item item : items) {
			int type = item.getType();
			switch (type) {
				case 0: {
					backgrounds.add(item);
					break;
				}
				case 1: {
					hats.add(item);
					break;
				}
				case 2: {
					bodies.add(item);
					break;
				}
				case 3: {
					paws.add(item);
					break;
				}
				case 4: {
					tails.add(item);
					break;
				}
				case 5: {
					shirts.add(item);
					break;
				}
				case 6: {
					noses.add(item);
					break;
				}
				case 7: {
					eyes.add(item);
					break;
				}
				case 8: {
					neck.add(item);
					break;
				}
			}
		}
		return new Inventory(user, backgrounds, hats, bodies, paws, tails, shirts, noses, eyes, neck);
	}

	public static boolean hasAllItems(IUser user) {
		List<Item> items = DataBase.queryItems(user);
		if (items == null) return false;
		return items.size() >= WolfCosmetics.totalItems;
	}

	public static boolean hasAllItems(List<Item> items) {
		return items.size() >= WolfCosmetics.totalItems;
	}

	public static EmbedObject getFoods() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.withColor(Color.CYAN);
		builder.withTitle("All Foods");
		for (WolfCosmetics.foods food : WolfCosmetics.foods.values()) {
			builder.appendField(WordUtils.capitalizeFully(food.getName().toLowerCase()), "Value: " + food.getValue() + "\nCooldown: " + Utility.formatTime(food.getCooldown()), false);
		}
		return builder.build();
	}

	public static Item getItemByPath(String path) {
		for (WolfCosmetics.backgrounds item : WolfCosmetics.backgrounds.values()) {
			if (item.getFile().equals(path)) return new Item(item.getFile(), item.getName(), 0);
		}
		for (WolfCosmetics.hats item : WolfCosmetics.hats.values()) {
			if (item.getFile().equals(path)) return new Item(item.getFile(), item.getName(), 1);
		}
		for (WolfCosmetics.bodies item : WolfCosmetics.bodies.values()) {
			if (item.getFile().equals(path)) return new Item(item.getFile(), item.getName(), 2);
		}
		for (WolfCosmetics.paws item : WolfCosmetics.paws.values()) {
			if (item.getFile().equals(path)) return new Item(item.getFile(), item.getName(), 3);
		}
		for (WolfCosmetics.tails item : WolfCosmetics.tails.values()) {
			if (item.getFile().equals(path)) return new Item(item.getFile(), item.getName(), 4);
		}
		for (WolfCosmetics.shirts item : WolfCosmetics.shirts.values()) {
			if (item.getFile().equals(path)) return new Item(item.getFile(), item.getName(), 5);
		}
		for (WolfCosmetics.noses item : WolfCosmetics.noses.values()) {
			if (item.getFile().equals(path)) return new Item(item.getFile(), item.getName(), 6);
		}
		for (WolfCosmetics.eyes item : WolfCosmetics.eyes.values()) {
			if (item.getFile().equals(path)) return new Item(item.getFile(), item.getName(), 7);
		}
		for (WolfCosmetics.neck item : WolfCosmetics.neck.values()) {
			if (item.getFile().equals(path)) return new Item(item.getFile(), item.getName(), 8);
		}

		return null;
	}

	public static Item getItemByName(String name) {
		for (WolfCosmetics.backgrounds item : WolfCosmetics.backgrounds.values()) {
			if (item.getName().equals(name)) return new Item(item.getFile(), item.getName(), 0);
		}
		for (WolfCosmetics.hats item : WolfCosmetics.hats.values()) {
			if (item.getName().equals(name)) return new Item(item.getFile(), item.getName(), 1);
		}
		for (WolfCosmetics.bodies item : WolfCosmetics.bodies.values()) {
			if (item.getName().equals(name)) return new Item(item.getFile(), item.getName(), 2);
		}
		for (WolfCosmetics.paws item : WolfCosmetics.paws.values()) {
			if (item.getName().equals(name)) return new Item(item.getFile(), item.getName(), 3);
		}
		for (WolfCosmetics.tails item : WolfCosmetics.tails.values()) {
			if (item.getName().equals(name)) return new Item(item.getFile(), item.getName(), 4);
		}
		for (WolfCosmetics.shirts item : WolfCosmetics.shirts.values()) {
			if (item.getName().equals(name)) return new Item(item.getFile(), item.getName(), 5);
		}
		for (WolfCosmetics.noses item : WolfCosmetics.noses.values()) {
			if (item.getName().equals(name)) return new Item(item.getFile(), item.getName(), 6);
		}
		for (WolfCosmetics.eyes item : WolfCosmetics.eyes.values()) {
			if (item.getName().equals(name)) return new Item(item.getFile(), item.getName(), 7);
		}
		for (WolfCosmetics.neck item : WolfCosmetics.neck.values()) {
			if (item.getName().equals(name)) return new Item(item.getFile(), item.getName(), 8);
		}

		return null;
	}

	public static Item dropItem(IUser user) {
		return randomItem(DataBase.queryItems(user));
	}

	public static List<Item> getAllItems() {
		List<Item> items = new ArrayList<>();
		for (WolfCosmetics.backgrounds item : WolfCosmetics.backgrounds.values()) {
			items.add(new Item(item.getFile(), item.getName(), 0));
		}
		for (WolfCosmetics.hats item : WolfCosmetics.hats.values()) {
			items.add(new Item(item.getFile(), item.getName(), 1));
		}
		for (WolfCosmetics.bodies item : WolfCosmetics.bodies.values()) {
			items.add(new Item(item.getFile(), item.getName(), 2));
		}
		for (WolfCosmetics.paws item : WolfCosmetics.paws.values()) {
			items.add(new Item(item.getFile(), item.getName(), 3));
		}
		for (WolfCosmetics.tails item : WolfCosmetics.tails.values()) {
			items.add(new Item(item.getFile(), item.getName(), 4));
		}
		for (WolfCosmetics.shirts item : WolfCosmetics.shirts.values()) {
			items.add(new Item(item.getFile(), item.getName(), 5));
		}
		for (WolfCosmetics.noses item : WolfCosmetics.noses.values()) {
			items.add(new Item(item.getFile(), item.getName(), 6));
		}
		for (WolfCosmetics.eyes item : WolfCosmetics.eyes.values()) {
			items.add(new Item(item.getFile(), item.getName(), 7));
		}
		for (WolfCosmetics.neck item : WolfCosmetics.neck.values()) {
			items.add(new Item(item.getFile(), item.getName(), 8));
		}
		return items;
	}

	public static List<Item> getUnownedItems(List<Item> owned) {
		List<Item> unowned = getAllItems().stream().filter(i -> !i.getName().toUpperCase().contains("NONE")).collect(Collectors.toList());
		unowned.removeAll(owned);
		return unowned;
	}

	public static Item randomItem(List<Item> items) {
		Item item = null;

		List<Item> unowned = getUnownedItems(items).stream().filter(i -> !i.getName().contains("NONE")).collect(Collectors.toList());
		if (unowned.size() > 0) {
			item = unowned.get(new Random().nextInt(unowned.size()));
		}
		return item;
	}

	public static String getItemStringType(Item item) {
		switch (item.getType()) {
			case 0: {
				return "background";
			}
			case 1: {
				return "hat";
			}
			case 2: {
				return "body";
			}
			case 3: {
				return "paws";
			}
			case 4: {
				return "tail";
			}
			case 5: {
				return "shirt";
			}
			case 6: {
				return "nose";
			}
			case 7: {
				return "eye";
			}
			case 8: {
				return "neck";
			}
		}
		return null;
	}

	public static EmbedObject scanMessageAndAction(IGuild guild, IMessage message) {
		String content = message.getContent();
		List<IMessage.Attachment> attachments = message.getAttachments();
		List<IChannel> channelMentions = message.getChannelMentions();
		List<IRole> roleMentions = message.getRoleMentions();
		List<IUser> userMentions = message.getMentions();

		/*for (String bannedString : DataBase.getBannedStrings(guild)) {
			if (content.contains(bannedString)) {
				return generateScanEmbed(message.getAuthor(), Color.RED);
			}
		}*/
		return null;
	}

	public static EmbedObject generateScanEmbed(IGuild guild, IUser author, Color c, FilterReason reason) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.withColor(c);
		builder.withTitle(String.format(Localisation.getMessage(guild.getStringID(), "filtered-message"), author));
		builder.appendField("Reason", reason.getHumanReadable(), true);

		return builder.build();
	}

	public static String formatWelcome(IGuild guild, IUser user, String welcome) {
		welcome = welcome.replace("{guild}", guild.getName());
		welcome = welcome.replace("{name}", user.getName());
		welcome = welcome.replace("{mention}", user.mention());
		return welcome;
	}

	public static boolean checkUserPermission(IGuild guild, IUser user, Permissions permission) {
		return (user.getPermissionsForGuild(guild).contains(permission));
	}

	@Deprecated
	public static String getChannelMod(String channelID) {
		return DataBase.channelQuery(channelID);
	}

	public static void storeChannelMod(String channelID, String mod) {
		DataBase.insertChannel(channelID, mod);
		DataBase.updateChannel(channelID, mod);
	}

	public static void postStats(int shard, int shardCount, int serverCount) {
		HTTPHandler.postStats(shard, shardCount, serverCount);
	}

	public static EmbedObject banUser(IGuild guild, IUser user, IUser author, String reason) {
		EmbedBuilder builder = new EmbedBuilder();
		try {
			RequestBuffer.request(() -> {
				guild.banUser(user);
			});

			builder.withAuthorIcon(Utility.getAvatar(user));
			builder.withAuthorName("User " + user.getName() + " was banned");
			builder.withColor(Color.RED);
			builder.withTimestamp(LocalDateTime.now());
			builder.withThumbnail(Utility.getAvatar(user));

			builder.appendField("User banned", user.getName(), true);
			builder.appendField("ID", user.getStringID(), true);
			builder.appendField("Banned by", author.getName(), true);
			builder.appendField("ID", author.getStringID(), true);
			builder.appendField("Reason", reason, false);
		} catch (MissingPermissionsException e) {
			builder.withTitle("Error");
			if (e.getMissingPermissions().size() == 0) {
				builder.withDescription(String.format(Localisation.getMessage(guild.getStringID(), "failed-ban"), user.getName(), "Role hierarchy is too high"));
			} else {
				builder.withDescription(String.format(Localisation.getMessage(guild.getStringID(), "failed-ban"), user.getName(), Utility.permissionsAsString(e.getMissingPermissions())));
			}
		}
		return builder.build();
	}

	public static EmbedObject kickUser(IGuild guild, IUser user, IUser author, String reason) {
		EmbedBuilder builder = new EmbedBuilder();
		try {
			RequestBuffer.request(() -> {
				guild.kickUser(user);
			});

			builder.withAuthorIcon(Utility.getAvatar(user));
			builder.withAuthorName("User " + user.getName() + " was kicked");
			builder.withColor(Color.RED);
			builder.withTimestamp(LocalDateTime.now());
			builder.withThumbnail(Utility.getAvatar(user));

			builder.appendField("User kicked", user.getName(), true);
			builder.appendField("ID", user.getStringID(), true);
			builder.appendField("Banned by", author.getName(), true);
			builder.appendField("ID", author.getStringID(), true);
			builder.appendField("Reason", reason, false);
		} catch (MissingPermissionsException e) {
			builder.withTitle("Error");
			if (e.getMissingPermissions().size() == 0) {
				builder.withDescription(String.format(Localisation.getMessage(guild.getStringID(), "failed-kick"), user.getName(), "Role hierarchy is too high"));
			} else {
				builder.withDescription(String.format(Localisation.getMessage(guild.getStringID(), "failed-kick"), user.getName(), Utility.permissionsAsString(e.getMissingPermissions())));
			}
		}
		return builder.build();
	}

	public static IChannel getLogChannel(IGuild guild) {
		for (IChannel channel : guild.getChannels()) {
			if (DataBase.channelQuery(channel.getStringID()).equals("log")) return channel;
		}
		return null;
	}

	public static IChannel getLogChannel(IGuild guild, IChannel defaultTo) {
		for (IChannel channel : guild.getChannels()) {
			if (DataBase.channelQuery(channel.getStringID()).equals("log")) return channel;
		}
		return defaultTo;
	}

	public static EmbedObject getStats(int shard, int shardCount, int serverCount) {
		updateStats();
		if (!Config.DEBUG) postStats(shard, shardCount, serverCount);

		EmbedBuilder builder = new EmbedBuilder();
		builder.withColor(Color.CYAN);
		builder.withTimestamp(LocalDateTime.now());
		builder.withAuthorName("HoroBot");
		builder.withAuthorIcon(Utility.getAvatar(Main.INSTANCE.client.getOurUser()));
		builder.withThumbnail(Utility.getAvatar(Main.INSTANCE.client.getOurUser()));

		builder.appendField("Discord4J Version", Discord4JVersion, false);
		builder.appendField("Shards", String.valueOf(shardCount), true);
		builder.appendField("Guilds", String.valueOf(serverCount), true);
		builder.appendField("Commands Executed", String.valueOf(commandsExecuted), true);
		builder.appendField("Commands Available", String.valueOf(totalCommands), true);
		builder.appendField("App Start", runStart, true);
		builder.appendField("App Up", runTime, true);
		builder.appendField("Reconnected Times", String.valueOf(reconnectedTimes), true);
		builder.appendField("Memory Usage", memory, true);
		builder.appendField("Messages Received", String.valueOf(messagesReceived), true);
		builder.appendField("Messages Sent", String.valueOf(messagesSent), true);

		return builder.build();
	}

	public static void updateStats() {
		totalCommands = Main.commands.size();
		runStart = getStartTime();
		runTime = getUpTime();
		memory = getMemoryUsage();
	}

	public static String getUpTime() {
		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		long uptime = rb.getUptime();

		int days = (int) TimeUnit.MILLISECONDS.toDays(uptime);
		int hours = (int) TimeUnit.MILLISECONDS.toHours(uptime) - (days * 24);
		int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(uptime) - (TimeUnit.MILLISECONDS.toHours(uptime) * 60));
		int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(uptime) - (TimeUnit.MILLISECONDS.toMinutes(uptime) * 60));
		return String.format("%d days, %d hours, %d minutes, %d seconds",
				days,
				hours,
				minutes,
				seconds
		);
	}

	public static String formatTime(long milliseconds) {
		StringBuilder builder = new StringBuilder();
		long convert = (milliseconds / 1000);
		int days = (int) (convert / (3600 * 24));
		int hours = (int) (convert / 3600 % 24);
		int minutes = (int) (convert / 60 % 60);
		int seconds = (int) (convert % 60);

		if (days > 0) {
			builder.append(days);
			builder.append(":");
		}

		if (hours > 0) {
			if (hours < 10)
				builder.append("0");
			builder.append(hours);
			builder.append(":");
		} else if (days > 0) {
			builder.append("00:");
		}

		if (minutes > 0) {
			if (minutes < 10 && (hours > 0 || days > 0))
				builder.append("0");
			builder.append(minutes);
		} else {
			builder.append("0");
			if (hours > 0 || days > 0)
				builder.append("0");
		}

		builder.append(":");
		if (seconds < 10)
			builder.append("0");
		builder.append(seconds);

		if (convert < 1f && convert > 0f) {
			return "0:0" + String.format("%.3f", Math.max(convert, 0.001f));
		}

		return builder.toString();
	}

	public static String getStartTime() {
		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		long startTime = rb.getStartTime();

		Date date = new Date(startTime);
		SimpleDateFormat format = new SimpleDateFormat();
		return format.format(date);
	}

	public static String getMemoryUsage() {
		Runtime runtime = Runtime.getRuntime();
		NumberFormat format = NumberFormat.getInstance();
		StringBuilder builder = new StringBuilder();

		long mb = 1024 * 1024;
		builder.append(format.format((runtime.totalMemory() - runtime.freeMemory()) / mb)).append("Mb / ");
		builder.append(format.format(runtime.totalMemory() / mb)).append("Mb");
		return builder.toString();
	}

	public static String getAvatar(IUser user) {
		return user.getAvatar() != null ? user.getAvatarURL() : getDefaultAvatar(user);
	}

	public static String getDefaultAvatar(IUser user) {
		int discrim = Integer.parseInt(user.getDiscriminator());
		discrim %= defaults.length;
		return "https://discordapp.com/assets/" + defaults[discrim] + ".png";
	}

	public static BufferedImage imageFor(String url) throws IOException {
		if (url == null)
			return null;
		URL urll = new URL(url);
		URLConnection connection = urll.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 Apache the Attack Helicopter");
		connection.connect();
		InputStream i = connection.getInputStream();
		BufferedImage b = ImageIO.read(i);
		i.close();
		return b;
	}

	public static String listAsString(List<String> list) {
		StringBuilder builder = new StringBuilder();
		for (String string : list) {
			builder.append(string);
			builder.append(", ");
		}
		return builder.length() > 0 ? builder.substring(0, builder.length() - 2) : "";
	}

	public static String itemsAsString(List<Item> items) {
		StringBuilder builder = new StringBuilder();
		for (Item item : items) {
			if (!item.getName().contains("NONE")) {
				builder.append(WordUtils.capitalizeFully(item.getName().toLowerCase()));
				builder.append(", ");
			}
		}
		return builder.length() > 0 ? builder.substring(0, builder.length() - 2) : "There's nothing here";
	}

	public static String permissionsAsString(EnumSet<Permissions> permissions) {
		StringBuilder builder = new StringBuilder();
		for (Permissions perm : permissions) {
			builder.append(perm.name());
			builder.append(", ");
		}
		return builder.length() > 0 ? builder.substring(0, builder.length() - 2) : "";
	}

	public static void sendMissingPermissions(IChannel channel, MissingPermissionsException e) {
		String permissions;
		if (e.getMissingPermissions().size() == 0) {
			permissions = "Role hierarchy too high";
		} else {
			permissions = Utility.permissionsAsString(e.getMissingPermissions());
		}
		Message.sendMessageInChannel(channel, "missing-permissions", permissions);
	}

	public static void saveGifToStream(List<BufferedImage> images, OutputStream stream, int timeBetweenFrames, boolean loop) throws IOException {
		GifSequenceWriter writer = new GifSequenceWriter(ImageIO.createImageOutputStream(stream), images.get(0).getType(), timeBetweenFrames, loop);
		for (BufferedImage image : images) {
			writer.writeToSequence(image);
		}
		writer.close();
	}

	public static List<BufferedImage> getGifFramesFromStream(InputStream stream) throws IOException {
		GifDecoder gd = new GifDecoder();
		gd.read(stream);
		List<BufferedImage> frames = new ArrayList<>(gd.getFrameCount());
		for(int i = 0; i < gd.getFrameCount(); i++) {
			frames.add(gd.getFrame(i));
		}
		return frames;
	}

	public static BufferedImage copyImage(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

}