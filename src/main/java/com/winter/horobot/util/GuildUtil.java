package com.winter.horobot.util;

import com.winter.horobot.data.cache.GuildMeta;
import com.winter.horobot.data.cache.HoroCache;
import sx.blah.discord.handle.obj.IGuild;

import java.util.Collections;
import java.util.Set;

public class GuildUtil {

	private static final String DEFAULT_PREFIX = ".horo";

	public static Set<String> getPrefixes(IGuild g) {
		GuildMeta m = HoroCache.get(g);
		if (m.getPrefixes().isEmpty()) {
			return Collections.singleton(DEFAULT_PREFIX);
		} else {
			Set<String> prefixes = m.getPrefixes();
			prefixes.add(".horo");
			return prefixes;
		}
	}
}
