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

package com.winter.horobot.core;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public class ClientManager {
	
	public IDiscordClient client;
	
	public ClientManager(IDiscordClient reference) {
		client = reference;
	}
	
	public static ClientManager createClient() {
		String token;
		if (Config.DEBUG) {
			token = Config.DEBUG_TOKEN;
		} else {
			token = Config.RELEASE_TOKEN;
		}
		ClientManager bot = null;
		
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(token);
		clientBuilder.withRecommendedShardCount();
		
		try {
			IDiscordClient client = clientBuilder.login();
			bot = new ClientManager(client);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return bot;
	}
}