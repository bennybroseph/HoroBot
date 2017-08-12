package com.winter.horobot.command;

import com.tsunderebug.iaatmt.jsonapis.KonaChan;
import com.winter.horobot.Main;
import com.winter.horobot.checks.ChannelChecks;
import com.winter.horobot.data.cache.HoroCache;
import com.winter.horobot.data.locale.Localisation;
import com.winter.horobot.data.Node;
import com.winter.horobot.checks.PermissionChecks;
import com.winter.horobot.exceptions.ErrorHandler;
import com.winter.horobot.exceptions.UpdateFailedException;
import com.winter.horobot.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Commands implements IListener<MessageReceivedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Commands.class);

	enum Category {
		ADMIN("admin"),
		FUN("fun"),
		DEV("developer"),
		STATUS("status");

		private final String name;

		Category(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	private static final Map<Category, List<Node<Command>>> COMMAND_MAP = new EnumMap<>(Category.class);
	private static final List<Node<Command>> COMMANDS = new ArrayList<>();

	static {
		COMMAND_MAP.put(Category.DEV, new ArrayList<>(Arrays.asList(
				new Node<>(new Command(
						"set",
						PermissionChecks.isGlobal(),
						e -> false
				), Arrays.asList(
						new Node<>(new Command(
								"playing",
								PermissionChecks.isGlobal(),
								e -> {
									Main.getClient().changePlayingText(MessageUtil.args(e.getMessage()).substring("set playing".length()));
									return true;
								}
						), Collections.emptyList())
				))
		)));
		COMMAND_MAP.put(Category.STATUS, new ArrayList<>(Arrays.asList(
				new Node<>(new Command(
						"help",
						PermissionChecks.hasPermision(Permissions.SEND_MESSAGES),
						Commands::sendHelp
				), Collections.emptyList()),
				new Node<>(new Command(
						"ping",
						PermissionChecks.hasPermision(Permissions.SEND_MESSAGES),
						StatusUtil::ping,
						new HashSet<>(Collections.singleton("pong"))
				), Collections.emptyList()),
				new Node<>(new Command(
						"hi",
						PermissionChecks.hasPermision(Permissions.SEND_MESSAGES),
						e -> {
							e.getChannel().sendMessage(Localisation.of(e.getGuild(), "hello"));
							return true;
						}
				), Collections.emptyList())
		)));
		COMMAND_MAP.put(Category.FUN, new ArrayList<>(Arrays.asList(
				new Node<>(new Command(
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
				), Collections.emptyList()),
				new Node<>(new Command(
						"kona",
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
				))
		)));
		COMMAND_MAP.put(Category.ADMIN, new ArrayList<>(Arrays.asList(
				new Node<>(new Command(
						"kick",
						PermissionChecks.hasPermision(Permissions.KICK),
						AdminUtil::kick
				), Collections.emptyList()),
				new Node<>(new Command(
						"ban",
						PermissionChecks.hasPermision(Permissions.BAN),
						AdminUtil::ban
				), Collections.emptyList()),
				new Node<>(new Command(
						"prefix",
						PermissionChecks.hasPermision(Permissions.SEND_MESSAGES),
						e -> {
							MessageUtil.sendMessage(e.getChannel(), "prefixes", Arrays.toString(GuildUtil.getPrefixes(e.getGuild()).toArray()));
							return true;
						}
				), Arrays.asList(
						new Node<>(new Command(
								"add",
								PermissionChecks.hasPermision(Permissions.MANAGE_SERVER),
								e -> {
									try {
										HoroCache.get(e.getGuild()).addPrefix(MessageUtil.args(e.getMessage()).substring("prefix add ".length()));
										MessageUtil.sendMessage(e.getChannel(), "added-prefix");
									} catch (UpdateFailedException ex) {
										ErrorHandler.log(ex, e.getChannel());
									}
									return true;
								}
						), Collections.emptyList()),
						new Node<>(new Command(
								"remove",
								PermissionChecks.hasPermision(Permissions.MANAGE_SERVER),
								e -> {
									try {
										HoroCache.get(e.getGuild()).removePrefix(MessageUtil.args(e.getMessage()).substring("prefix remove ".length()));
										MessageUtil.sendMessage(e.getChannel(), "removed-prefix");
									} catch (UpdateFailedException ex) {
										ErrorHandler.log(ex, e.getChannel());
									}
									return true;
								}
						), Collections.emptyList())
				))
		)));

		COMMAND_MAP.values().forEach(COMMANDS::addAll);
	}

	private static boolean sendHelp(MessageReceivedEvent e) {
		try {
			IChannel dmChannel = e.getAuthor().getOrCreatePMChannel();
			for (Map.Entry<Category, List<Node<Command>>> c : COMMAND_MAP.entrySet()) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.withColor(Color.MAGENTA.darker());
				eb.withTitle(Localisation.of(e.getGuild(), c.getKey().getName()) + " " + Localisation.of(e.getGuild(), "command"));
				StringBuilder desc = new StringBuilder();
				for (Node<Command> n : c.getValue()) {
					desc.append("`").append(n.getData().getName()).append("`\n");
				}
				eb.appendDescription(desc.toString());
				dmChannel.sendMessage(eb.build());
			}
			return true;
		} catch (DiscordException de) {
			return false;
		}
	}

	@Override
	public void handle(MessageReceivedEvent e) {
		try {
			if (GuildUtil.getPrefixes(e.getGuild()).stream().anyMatch(e.getMessage().getContent()::startsWith)) {
				String lookingFor = MessageUtil.args(e.getMessage());
				COMMANDS.forEach(n -> {
					Node<Command> gotten = n.traverseThis(node -> node.getData().getAliases().stream().map(s -> {
						if (node.getParent() != null) {
							return node.getParent().compileTopDown(Command::getName, (s1, s2) -> s1 + " " + s2) + " " + s;
						} else {
							return s;
						}
					}).collect(Collectors.toSet()), lookingFor, (t, m) -> m.startsWith(t + " ") || m.endsWith(t), false);
					if (gotten != null) {
						LOGGER.debug(String.format("Found `%s`", gotten.getData().getName()));
						e.getChannel().setTypingStatus(true);
						gotten.getData().call(e);
						e.getChannel().setTypingStatus(false);
					}
				});
			}
		} catch (Exception ex) {
			ErrorHandler.log(ex, e.getChannel());
			e.getChannel().setTypingStatus(false);
		}
	}
}