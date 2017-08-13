package com.winter.horobot.command;

import com.winter.horobot.command.impl.developer.*;
import com.winter.horobot.command.impl.status.*;
import com.winter.horobot.command.impl.fun.*;
import com.winter.horobot.command.impl.admin.*;
import com.winter.horobot.data.locale.Localisation;
import com.winter.horobot.data.Node;
import com.winter.horobot.exceptions.ErrorHandler;
import com.winter.horobot.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
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
		COMMAND_MAP.put(Category.DEV, new ArrayList<>(Arrays.asList(new CommandSet())));
		COMMAND_MAP.put(Category.STATUS, new ArrayList<>(Arrays.asList(new CommandHelp(), new CommandPing(), new CommandHi())));
		COMMAND_MAP.put(Category.FUN, new ArrayList<>(Arrays.asList(new CommandHue(), new CommandKona())));
		COMMAND_MAP.put(Category.ADMIN, new ArrayList<>(Arrays.asList(new CommandKick(), new CommandBan(), new CommandPrefix())));

		COMMAND_MAP.values().forEach(COMMANDS::addAll);
	}

	public static boolean sendHelp(MessageReceivedEvent e) {
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