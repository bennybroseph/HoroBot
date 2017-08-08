package com.winter.horobot.data.cache;

import com.winter.horobot.data.Database;
import com.winter.horobot.exceptions.UpdateFailedException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.Ban;
import sx.blah.discord.util.Image;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GuildMeta implements IGuild {

	private final IGuild guild;
	private Set<String> prefixes;
	private Set<Long> autoroles;
	private String language;
	private String welcome;
	private String pm;
	private boolean levelupNotifications;
	private boolean presentBan;
	private boolean botIgnore;

	/**
	 * Constructor for guild metadata object
	 * @param guild The guild object to construct metadata for
	 */
	public GuildMeta(IGuild guild) {
		this.guild = guild;
		Map<String, Object> settings = Database.get("SELECT * FROM guilds.guild WHERE id=?", guild.getStringID());
		this.prefixes = Arrays.stream((String[]) settings.getOrDefault("prefixes", new String[]{})).collect(Collectors.toSet());

		// TODO: This is quite hacky, dunno if there is a better way vvvvvvv
		String[] roles = (String[]) settings.getOrDefault("autoroles", new String[]{});
		Long[] roless = new Long[roles.length];
		for (int i = 0; i < roles.length; i++)
			roless[i] = Long.parseUnsignedLong(roles[i]);

		this.autoroles = Arrays.stream(roless).collect(Collectors.toSet());
		this.language = (String) settings.getOrDefault("language", "en");
		this.welcome = (String) settings.getOrDefault("welcome", "none");
		this.pm = (String) settings.getOrDefault("pm", "none");
		this.levelupNotifications = (boolean) settings.getOrDefault("lvlup", true);
		this.presentBan = (boolean) settings.getOrDefault("bpresentban", true);
		this.botIgnore = (boolean) settings.getOrDefault("bignore", false);
	}

	public Set<String> getPrefixes() {
		return prefixes;
	}

	public void addPrefix(String prefix) throws UpdateFailedException {
		Set<String> prefixes = this.getPrefixes();
		if (prefixes.contains(prefix)) // Just to be sure
			throw new UpdateFailedException("Duplicate prefix");
		prefixes.add(prefix);
		if (!Database.set("UPDATE guilds.guild SET prefixes=? WHERE id=?", (String[]) prefixes.toArray(), this.guild.getStringID()))
			throw new UpdateFailedException("Failed to update guild metadata");
		this.prefixes = prefixes;
	}

	public void removePrefix(String prefix) throws UpdateFailedException {
		Set<String> prefixes = this.getPrefixes();
		if (!prefixes.contains(prefix))
			throw new UpdateFailedException("Prefix does not exist");
		prefixes.remove(prefix);
		if (!Database.set("UPDATE guilds.guild SET prefixes=? WHERE id=?", prefixes.toArray(), this.guild.getStringID()))
			throw new UpdateFailedException("Failed to update guild metadata");
		this.prefixes = prefixes;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) throws UpdateFailedException {
		if (!Database.set("UPDATE guilds.guild SET language=? WHERE id=?", language, this.getStringID()))
			throw new UpdateFailedException("Failed to update guild metadata");
		this.language = language;
	}

	public String getWelcome() {
		return welcome;
	}

	public void setWelcome(String welcome) throws UpdateFailedException {
		if (!Database.set("UPDATE guilds.guild SET welcome=? WHERE id=?", welcome, this.getStringID()))
			throw new UpdateFailedException("Failed to update guild metadata");
		this.welcome = welcome;
	}

	public String getPm() {
		return pm;
	}

	public void setPm(String pm) throws UpdateFailedException {
		if (!Database.set("UPDATE guilds.guild SET pm=? WHERE id=?", pm, this.getStringID()))
			throw new UpdateFailedException("Failed to update guild metadata");
		this.pm = pm;
	}

	public Set<Long> getAutorole() {
		return autoroles;
	}

	public void setAutorole(Set<Long> autoroles) throws UpdateFailedException {
		if (!Database.set("UPDATE guilds.guild SET autoroles=? WHERE id=?", autoroles, this.getStringID()))
			throw new UpdateFailedException("Failed to update guild metadata");
		this.autoroles = autoroles;
	}

	public boolean isLevelupNotifications() {
		return levelupNotifications;
	}

	public void setLevelupNotifications(boolean levelupNotifications) throws UpdateFailedException {
		if (!Database.set("UPDATE guilds.guild SET lvlup=? WHERE id=?", levelupNotifications, this.getStringID()))
			throw new UpdateFailedException("Failed to update guild metadata");
		this.levelupNotifications = levelupNotifications;
	}

	public boolean isPresentBan() {
		return presentBan;
	}

	public void setPresentBan(boolean presentBan) throws UpdateFailedException {
		if (!Database.set("UPDATE guilds.guild SET bpresentban=? WHERE id=?", presentBan, this.getStringID()))
			throw new UpdateFailedException("Failed to update guild metadata");
		this.presentBan = presentBan;
	}

	public boolean isBotIgnore() {
		return botIgnore;
	}

	public void setBotIgnore(boolean botIgnore) throws UpdateFailedException {
		if (!Database.set("UPDATE guilds.guild SET bignore=? WHERE id=?", botIgnore, this.getStringID()))
			throw new UpdateFailedException("Failed to update guild metadata");
		this.botIgnore = botIgnore;
	}

	@Override
	public long getOwnerLongID() {
		return guild.getOwnerLongID();
	}

	@Override
	public IUser getOwner() {
		return guild.getOwner();
	}

	@Override
	public String getIcon() {
		return guild.getIcon();
	}

	@Override
	public String getIconURL() {
		return guild.getIconURL();
	}

	@Override
	public List<IChannel> getChannels() {
		return guild.getChannels();
	}

	@Override
	public IChannel getChannelByID(long l) {
		return guild.getChannelByID(l);
	}

	@Override
	public List<IUser> getUsers() {
		return guild.getUsers();
	}

	@Override
	public IUser getUserByID(long l) {
		return guild.getUserByID(l);
	}

	@Override
	public List<IChannel> getChannelsByName(String s) {
		return guild.getChannelsByName(s);
	}

	@Override
	public List<IVoiceChannel> getVoiceChannelsByName(String s) {
		return guild.getVoiceChannelsByName(s);
	}

	@Override
	public List<IUser> getUsersByName(String s) {
		return guild.getUsersByName(s);
	}

	@Override
	public List<IUser> getUsersByName(String s, boolean b) {
		return guild.getUsersByName(s, b);
	}

	@Override
	public List<IUser> getUsersByRole(IRole iRole) {
		return guild.getUsersByRole(iRole);
	}

	@Override
	public String getName() {
		return guild.getName();
	}

	@Override
	public List<IRole> getRoles() {
		return guild.getRoles();
	}

	@Override
	public List<IRole> getRolesForUser(IUser iUser) {
		return guild.getRolesForUser(iUser);
	}

	@Override
	public IRole getRoleByID(long l) {
		return guild.getRoleByID(l);
	}

	@Override
	public List<IRole> getRolesByName(String s) {
		return guild.getRolesByName(s);
	}

	@Override
	public List<IVoiceChannel> getVoiceChannels() {
		return guild.getVoiceChannels();
	}

	@Override
	public IVoiceChannel getVoiceChannelByID(long l) {
		return guild.getVoiceChannelByID(l);
	}

	@Override
	public IVoiceChannel getConnectedVoiceChannel() {
		return guild.getConnectedVoiceChannel();
	}

	@Override
	public IVoiceChannel getAFKChannel() {
		return guild.getAFKChannel();
	}

	@Override
	public int getAFKTimeout() {
		return guild.getAFKTimeout();
	}

	@Override
	public IRole createRole() {
		return guild.createRole();
	}

	@Override
	public List<IUser> getBannedUsers() {
		return guild.getBannedUsers();
	}

	@Override
	public List<Ban> getBans() {
		return guild.getBans();
	}

	@Override
	public void banUser(IUser iUser) {
		guild.banUser(iUser);
	}

	@Override
	public void banUser(IUser iUser, int i) {
		guild.banUser(iUser, i);
	}

	@Override
	public void banUser(IUser iUser, String s) {
		guild.banUser(iUser, s);
	}

	@Override
	public void banUser(IUser iUser, String s, int i) {
		guild.banUser(iUser, s, i);
	}

	@Override
	public void banUser(long l) {
		guild.banUser(l);
	}

	@Override
	public void banUser(long l, int i) {
		guild.banUser(l, i);
	}

	@Override
	public void banUser(long l, String s) {
		guild.banUser(l, s);
	}

	@Override
	public void banUser(long l, String s, int i) {
		guild.banUser(l, s, i);
	}

	@Override
	public void pardonUser(long l) {
		guild.pardonUser(l);
	}

	@Override
	public void kickUser(IUser iUser) {
		guild.kickUser(iUser);
	}

	@Override
	public void kickUser(IUser iUser, String s) {
		guild.kickUser(iUser, s);
	}

	@Override
	public void editUserRoles(IUser iUser, IRole[] iRoles) {
		guild.editUserRoles(iUser, iRoles);
	}

	@Override
	public void setDeafenUser(IUser iUser, boolean b) {
		guild.setDeafenUser(iUser, b);
	}

	@Override
	public void setMuteUser(IUser iUser, boolean b) {
		guild.setMuteUser(iUser, b);
	}

	@Override
	public void setUserNickname(IUser iUser, String s) {
		guild.setUserNickname(iUser, s);
	}

	@Override
	public void edit(String s, IRegion iRegion, VerificationLevel verificationLevel, Image image, IVoiceChannel iVoiceChannel, int i) {
		guild.edit(s, iRegion, verificationLevel, image, iVoiceChannel, i);
	}

	@Override
	public void changeName(String s) {
		guild.changeName(s);
	}

	@Override
	public void changeRegion(IRegion iRegion) {
		guild.changeRegion(iRegion);
	}

	@Override
	public void changeVerificationLevel(VerificationLevel verificationLevel) {
		guild.changeVerificationLevel(verificationLevel);
	}

	@Override
	public void changeIcon(Image image) {
		guild.changeIcon(image);
	}

	@Override
	public void changeAFKChannel(IVoiceChannel iVoiceChannel) {
		guild.changeAFKChannel(iVoiceChannel);
	}

	@Override
	public void changeAFKTimeout(int i) {
		guild.changeAFKTimeout(i);
	}

	@Override
	public void deleteGuild() {
		guild.deleteGuild();
	}

	@Override
	public void leaveGuild() {
		guild.leaveGuild();
	}

	@Override
	public void leave() {
		guild.leave();
	}

	@Override
	public IChannel createChannel(String s) {
		return guild.createChannel(s);
	}

	@Override
	public IVoiceChannel createVoiceChannel(String s) {
		return guild.createVoiceChannel(s);
	}

	@Override
	public IRegion getRegion() {
		return guild.getRegion();
	}

	@Override
	public VerificationLevel getVerificationLevel() {
		return guild.getVerificationLevel();
	}

	@Override
	public IRole getEveryoneRole() {
		return guild.getEveryoneRole();
	}

	@Override
	public IChannel getGeneralChannel() {
		return guild.getGeneralChannel();
	}

	@Override
	public List<IInvite> getInvites() {
		return guild.getInvites();
	}

	@Override
	public List<IExtendedInvite> getExtendedInvites() {
		return guild.getExtendedInvites();
	}

	@Override
	public void reorderRoles(IRole... iRoles) {
		guild.reorderRoles(iRoles);
	}

	@Override
	public int getUsersToBePruned(int i) {
		return guild.getUsersToBePruned(i);
	}

	@Override
	public int pruneUsers(int i) {
		return guild.pruneUsers(i);
	}

	@Override
	public boolean isDeleted() {
		return guild.isDeleted();
	}

	@Override
	public IAudioManager getAudioManager() {
		return guild.getAudioManager();
	}

	@Override
	public LocalDateTime getJoinTimeForUser(IUser iUser) {
		return guild.getJoinTimeForUser(iUser);
	}

	@Override
	public IMessage getMessageByID(long l) {
		return guild.getMessageByID(l);
	}

	@Override
	public List<IEmoji> getEmojis() {
		return guild.getEmojis();
	}

	@Override
	public IEmoji getEmojiByID(long l) {
		return guild.getEmojiByID(l);
	}

	@Override
	public IEmoji getEmojiByName(String s) {
		return guild.getEmojiByName(s);
	}

	@Override
	public IWebhook getWebhookByID(long l) {
		return guild.getWebhookByID(l);
	}

	@Override
	public List<IWebhook> getWebhooksByName(String s) {
		return guild.getWebhooksByName(s);
	}

	@Override
	public List<IWebhook> getWebhooks() {
		return guild.getWebhooks();
	}

	@Override
	public int getTotalMemberCount() {
		return 0;
	}

	@Override
	public IDiscordClient getClient() {
		return guild.getClient();
	}

	@Override
	public IShard getShard() {
		return guild.getShard();
	}

	@Override
	public IGuild copy() {
		return guild.copy();
	}

	@Override
	public long getLongID() {
		return guild.getLongID();
	}
}