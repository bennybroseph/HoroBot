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

package com.winter.horobot.command.impl.fun;

import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.command.proccessing.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.Random;

public class CommandPornStar implements Command {
	
	protected final String[] pre = { "Wild", "Slay", "Young", "Super", "Mega", "Pro", "Waka",  "Freaky", "OT", "Unreal", "Focused", "8-Inch", "Cool", "Big", "Latin", "Spicy", "Extra" ,"Little" ,"Mr.", "Alpha", "", "Lonely", "Ultra", "MC.", "DJ.", "Grip", "Sad", "Cyber", "Black", "12-Inch", "White", "6-Inch", "Crazy" ,"Orgasmic", "3-Inch", "Tender", "Fresh", "Smooth", "Mean", "Dangerous", "Tiny", "Extended", "Amazing","Long", "Wobbly" }; 
	
	protected final String[] suf = { "God", "Hang", "Lad", "Penis", "Black", "RattleSnake", "Boy","Willie", "Missle", "Stick", "King", "Wanker", "Cock", "Queen", "BackSeat", "Johnson","Honey","Wiggle", "Ass" ,"Fat","Dick",  "Splash", "Titties", "Chode", "Commando" , "Shaft", "Swang", "Wizard", "Cobra", "Nasty", "Kardashian", "Slop", "Machine", "Cracker", "Sexy", "Slim", "Wet", "Drop", "Dip" };

	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if (args.length == 0) {
			String guildID = event.getMessage().getGuild().getStringID();
			
			Random rand = new Random();
			String prefix = pre[rand.nextInt(pre.length)];
			String suffix = suf[rand.nextInt(suf.length)];
			String name = prefix + " " + suffix;
			
			Message.sendMessageInChannel(event.getChannel(),"pornstar-name", event.getAuthor().getName(), name);
		} else {
			Message.reply(help(), event.getMessage());
		}
	}

	public String help() {
		return "ps-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.FUN;
	}

	public void executed(boolean success, MessageReceivedEvent event) {
		if(success)
			Utility.commandsExecuted++;
	}
}