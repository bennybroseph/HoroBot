package com.winter.horobot.command.impl.dev;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * I sexually Identify as an Attack Helicopter.
 * Ever since I was a boy I dreamed of soaring over the oilfields dropping hot sticky loads on disgusting foreigners.
 * People say to me that a person being a helicopter is Impossible and I'm fucking retarded but I don't care, I'm beautiful.
 * I'm having a plastic surgeon install rotary blades, 30 mm cannons and AMG-114 Hellfire missiles on my body.
 * From now on I want you guys to call me "Apache" and respect my right to kill from above and kill needlessly.
 * If you can't accept me you're a heliphobe and need to check your vehicle privilege.
 * Thank you for being so understanding.
 *
 * @author Apache the Attack Helicopter
 */
public class CommandEval implements Command {
	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getStringID().equals("288996157202497536")
				|| event.getAuthor().getStringID().equals("155954930191040513");
	}

	@Override
	public CommandType getType() {
		return CommandType.DEVELOPER;
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");
		engine.put("event", event);
		engine.put("client", event.getClient());
		engine.put("guild", event.getGuild());
		engine.put("channel", event.getChannel());

		try {
			String builder = "import java.lang.*;\n" +
					"import java.util.*;\n" +
					"import java.io.*;\n" +
					"import java.net.*;\n" +
					"import groovy.lang.*;\n" +
					"import groovy.util.*;\n" +
					"import java.math.BigInteger;\n" +
					"import java.math.BigDecimal;\n" +
					"import sx.blah.discord.handle.obj.*;\n" +
					"import sx.blah.discord.util.*;\n" +
					"import sx.blah.discord.api.internal.json.objects.*;\n" +
					"import java.awt.*;\n" +
					"import com.fuyusan.horobot.util.*;\n" +
					raw;
			event.getChannel().sendMessage(String.format("```groovy\n%s```", engine.eval(builder)));
		} catch (ScriptException e) {
			event.getChannel().sendMessage(String.format("```groovy\n%s```", e.getMessage()));
		}
	}

	@Override
	public String help() {
		return "eval-help";
	}
}
