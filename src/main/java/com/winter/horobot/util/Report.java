package com.winter.horobot.util;

import sx.blah.discord.handle.obj.IUser;

public class Report {

	private final String reportID;
	private final String messageID;
	private final IUser author;
	private final IUser target;
	private final String reason;


	public Report(String reportID, String messageID, IUser author, IUser target, String reason) {
		this.reportID = reportID;
		this.messageID = messageID;
		this.author = author;
		this.target = target;
		this.reason = reason;
	}

	public String getReportID() {
		return reportID;
	}

	public String getMessageID() { return messageID; }

	public IUser getAuthor() {
		return author;
	}

	public IUser getTarget() {
		return target;
	}

	public String getReason() {
		return reason;
	}
}
