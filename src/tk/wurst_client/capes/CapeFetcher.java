/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.capes;

import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import tk.wurst_client.utils.JsonUtils;
import tk.wurst_client.utils.MiscUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

public class CapeFetcher implements Runnable
{
	private HashMap<String, SkinAvailableCallback> callbacks = new HashMap<>();
	private boolean running = false;
	
	public boolean addUUID(String uuid, SkinAvailableCallback callback)
	{
		if(callbacks.size() < 100 && !running)
		{
			callbacks.put(uuid, callback);
			return true;
		}else
			return false;
	}
	
	@Override
	public void run()
	{
		try
		{
			Thread.sleep(1000);
		}catch(InterruptedException e1)
		{
			e1.printStackTrace();
		}
		running = true;
		String response = null;
		try
		{
			response =
				MiscUtils.post(
					new URL("https://www.wurst-capes.tk/cosmetics/"),
					JsonUtils.gson.toJson(JsonUtils.gson.toJsonTree(
						callbacks.keySet()).getAsJsonArray()),
					"application/json");
			JsonObject cosmetics =
				JsonUtils.jsonParser.parse(response).getAsJsonObject();
			
			for(Entry<String, JsonElement> entry : cosmetics.entrySet())
			{
				JsonObject playerCosmetics = entry.getValue().getAsJsonObject();
				Minecraft.getMinecraft().addScheduledTask(new Runnable()
				{
					@Override
					public void run()
					{
						SkinManager skinManager =
							Minecraft.getMinecraft().getSkinManager();
						if(playerCosmetics.has("skin"))
							
							skinManager.loadSkin(
								new MinecraftProfileTexture(playerCosmetics
									.get("skin").getAsString(), null),
								Type.SKIN, callbacks.get(entry.getKey()));
						if(playerCosmetics.has("cape"))
							skinManager.loadSkin(
								new MinecraftProfileTexture(playerCosmetics
									.get("cape").getAsString(), null),
								Type.CAPE, callbacks.get(entry.getKey()));
					}
				});
			}
		}catch(Exception e)
		{
			System.err.println("[Wurst] Failed to load " + callbacks.size()
				+ " cosmetic(s) from wurst-capes.tk!");
			System.out.println("Server response:\n" + response);
			e.printStackTrace();
		}
	}
}
