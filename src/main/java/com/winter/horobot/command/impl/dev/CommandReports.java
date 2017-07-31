package com.winter.horobot.command.impl.dev;

import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Report;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.RequestBuffer;

public class CommandReports implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return event.getAuthor().getStringID().equals("288996157202497536");
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if (args.length > 1) {
			Report report = DataBase.queryReport(args[0]);
			if (report != null) {
				if ("approve".equals(args[1])) {
					DataBase.insertGlobalBan(report.getTarget(), report.getReason());
					DataBase.closeReport(report.getReportID());
					Message.sendRawMessageInChannel(report.getTarget().getOrCreatePMChannel(), "Uh-oh! The report submitted against you has been approved and you have been banned from using HoroBot!\nDon't worry, there is still a chance of being pardoned for your actions! Simply join this guild https://discord.gg/MCUTSZz and ask one of the mods to pardon you, also give them a good reason why they should pardon you.");
					Message.sendRawMessageInChannel(report.getAuthor().getOrCreatePMChannel(), "The report you submitted against " + report.getTarget().getName() + " has been approved and the user is now banned from using HoroBot.\nThank you for keeping the HoroBot community clean!");
					RequestBuffer.request(() -> event.getClient().getMessageByID(Long.parseUnsignedLong(report.getMessageID())).delete());
				} else if ("deny".equals(args[1])) {
					Message.sendRawMessageInChannel(report.getAuthor().getOrCreatePMChannel(), "The report you submitted against " + report.getTarget().getName() + " has been denied and thus he won't be banned!\nTry providing better evidence against the target next time!");
					Message.sendRawMessageInChannel(report.getTarget().getOrCreatePMChannel(), "The report submitted against you has been denied and thus you won't be banned! Yaaay~!");
					RequestBuffer.request(() -> event.getClient().getMessageByID(Long.parseUnsignedLong(report.getMessageID())).delete());
				} else if ("abuse".equals(args[1])) {
					DataBase.insertGlobalBan(report.getAuthor(), "Abuse of the report system");
					DataBase.closeReport(report.getReportID());
					Message.sendRawMessageInChannel(report.getAuthor().getOrCreatePMChannel(), "Uh-oh! It seems like you abused the report system and it fired back at you! You have been banned from using HoroBot!\nDon't worry, there is still a chance of being pardoned for your actions! Simply join this guild https://discord.gg/MCUTSZz and ask one of the mods to pardon you, also give them a good reason why they should pardon you.");
					Message.sendRawMessageInChannel(report.getTarget().getOrCreatePMChannel(), "Oh well that turned out well for you! The report submitted against you has been denied and the submitter was banned from using HoroBot! Congratz~!");
					RequestBuffer.request(() -> event.getClient().getMessageByID(Long.parseUnsignedLong(report.getMessageID())).delete());
				}
			}
		}
	}

	@Override
	public String help() {
		return "report-help";
	}

	@Override
	public CommandType getType() {
		return CommandType.DEVELOPER;
	}
}