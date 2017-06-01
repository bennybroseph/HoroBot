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

package com.winter.horobot.command.proccessing;

import com.winter.horobot.core.Main;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Utility;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.Map;

public interface Command {

	default boolean called(String[] args, MessageReceivedEvent event) {
		return DataBase.queryPermissions(event.getGuild(), event.getAuthor()).contains(getPermission());
	}

	void action(String[] args, String raw, MessageReceivedEvent event);

	String help();

	CommandType getType();

	default String getPermission() {
		for (Map.Entry<String, Command> set : Main.commands.entrySet()) {
			if (set.getValue() == this) {
				return "horobot." + getType().getDescription().toLowerCase() + "." + set.getKey();
			}
		}
		return null;
	}

	default void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}
