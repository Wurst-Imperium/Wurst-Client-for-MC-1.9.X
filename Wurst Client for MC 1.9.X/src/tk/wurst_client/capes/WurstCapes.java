/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.capes;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import tk.wurst_client.utils.JsonUtils;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

public class WurstCapes
{
	private static JsonObject capes;
	private static CapeFetcher capeFetcher;
	
	/**
	 * @see net.minecraft.client.resources.SkinManager#func_152790_a(GameProfile,
	 *      SkinAvailableCallback, boolean)
	 * @param player
	 * @param skinManagerMap
	 * @param callback
	 */
	@SuppressWarnings("unchecked")
	public static void checkCape(GameProfile player, HashMap skinManagerMap,
		SkinAvailableCallback callback)
	{
		if(capes == null)
			try
			{
				HttpsURLConnection connection =
					(HttpsURLConnection)new URL(
						"https://www.wurst-client.tk/api/v1/capes.json")
						.openConnection();
				connection.connect();
				capes =
					JsonUtils.jsonParser.parse(
						new InputStreamReader(connection.getInputStream()))
						.getAsJsonObject();
			}catch(Exception e)
			{
				System.err
					.println("[Wurst] Failed to load capes from wurst-client.tk!");
				e.printStackTrace();
				return;
			}
		
		if(capes.has("use_new_server")
			&& capes.get("use_new_server").getAsBoolean())
			// get cape from new server
			try
			{
				String uuid = player.getId().toString().replace("-", "");
				if(capeFetcher == null || !capeFetcher.addUUID(uuid, callback))
				{
					capeFetcher = new CapeFetcher();
					capeFetcher.addUUID(uuid, callback);
					new Thread(capeFetcher).start();
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		else
			// get cape from old server
			try
			{
				if(capes.has(player.getName()))
					skinManagerMap.put(Type.CAPE, new MinecraftProfileTexture(
						capes.get(player.getName()).getAsString(), null));
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	}
}
