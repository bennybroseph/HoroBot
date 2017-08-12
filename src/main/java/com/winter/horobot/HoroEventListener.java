package com.winter.horobot;

import com.winter.horobot.data.cache.GuildMeta;
import com.winter.horobot.data.cache.HoroCache;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;

public class HoroEventListener {

	@EventSubscriber
	public void onGuildCreated(GuildCreateEvent e) {
		if (e.getClient().isReady() && e.getGuild() != null) {
			GuildMeta g = HoroCache.get(e.getGuild());
		}
	}
}