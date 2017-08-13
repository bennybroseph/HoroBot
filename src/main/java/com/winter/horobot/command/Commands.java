package com.winter.horobot.command;

import com.winter.horobot.command.impl.admin.CommandBan;
import com.winter.horobot.command.impl.admin.CommandKick;
import com.winter.horobot.command.impl.admin.CommandPrefix;
import com.winter.horobot.command.impl.developer.CommandSet;
import com.winter.horobot.command.impl.fun.CommandHue;
import com.winter.horobot.command.impl.fun.CommandKona;
import com.winter.horobot.command.impl.status.CommandHelp;
import com.winter.horobot.command.impl.status.CommandHi;
import com.winter.horobot.command.impl.status.CommandPing;
import com.winter.horobot.data.Node;
import com.winter.horobot.exceptions.ErrorHandler;
import com.winter.horobot.util.ColorUtil;
import com.winter.horobot.util.GuildUtil;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;

import java.util.*;
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

	public static final Map<Category, List<Node<Command>>> COMMAND_MAP = new EnumMap<>(Category.class);
	public static final List<Node<Command>> COMMANDS = new ArrayList<>();

	static {
		COMMAND_MAP.put(Category.DEV, new ArrayList<>(Arrays.asList(new CommandSet())));
		COMMAND_MAP.put(Category.STATUS, new ArrayList<>(Arrays.asList(new CommandHelp(), new CommandPing(), new CommandHi())));
		COMMAND_MAP.put(Category.FUN, new ArrayList<>(Arrays.asList(new CommandHue(), new CommandKona())));
		COMMAND_MAP.put(Category.ADMIN, new ArrayList<>(Arrays.asList(new CommandKick(), new CommandBan(), new CommandPrefix())));

		COMMAND_MAP.values().forEach(COMMANDS::addAll);
	}

	/**
	 * Sends the help
	 * @param e The event that triggered it
	 * @return True on success, false on failure
	 */
	public static boolean sendHelp(MessageReceivedEvent e) {
		try {
			EmbedBuilder eb = new EmbedBuilder();
			eb.withColor(ColorUtil.withinTwoHues(0.1f, 0.9f));
			eb.withTitle("Here's the full list of commands!");
			eb.withDescription("To get more information on a command use `.horohelp <command>` eg. `.horohelp ping`");
			for (Map.Entry<Category, List<Node<Command>>> c : COMMAND_MAP.entrySet()) {
				StringBuilder desc = new StringBuilder();
				for (Node<Command> n : c.getValue()) {
					desc.append("**").append(n.getData().getName()).append("**, ");
				}
				eb.appendField(WordUtils.capitalize(c.getKey().getName()), desc.toString().substring(0, desc.toString().length() - 2), false);
			}
			e.getChannel().sendMessage(eb.build());
			return true;
		} catch (DiscordException de) {
			return false;
		}
	}

	/**
	 * Handles MessageReceivedEvent and processes it
	 * @param e The event
	 */
	@Override
	public void handle(MessageReceivedEvent e) {
		try {
			Optional<String> o = GuildUtil.getPrefixes(e.getGuild()).stream().filter(e.getMessage().getContent()::startsWith).findFirst();
			if (o.isPresent()) {
				String lookingFor = Arrays.stream(e.getMessage().getContent().substring(o.get().length()).split("\\s+")).collect(Collectors.joining(" "));
				for (Node<Command> n : COMMANDS) {
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
						break;
					}
				}
			}
		} catch (Exception ex) {
			ErrorHandler.log(ex, e.getChannel());
			e.getChannel().setTypingStatus(false);
		}
	}
}
