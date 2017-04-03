/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.wurstclient.WurstClient;

public final class KeybindsConfig extends Config
{
	public KeybindsConfig()
	{
		super("keybinds.json");
	}
	
	@Override
	protected void loadFromJson(JsonElement json)
	{
		// clear keybinds
		WurstClient.INSTANCE.keybinds.clear();
		
		// add keybinds
		for(Entry<String, JsonElement> entry : json.getAsJsonObject()
			.entrySet())
		{
			TreeSet<String> commmands = new TreeSet<>();
			
			entry.getValue().getAsJsonArray()
				.forEach((c) -> commmands.add(c.getAsString()));
			
			WurstClient.INSTANCE.keybinds.put(entry.getKey(), commmands);
		}
		
		// force-add GUI keybind if missing
		if(!WurstClient.INSTANCE.keybinds
			.containsValue(new TreeSet<>(Arrays.asList(".t navigator"))))
		{
			WurstClient.INSTANCE.keybinds.put("LCONTROL", ".t navigator");
			save();
		}
	}
	
	@Override
	protected JsonElement saveToJson()
	{
		JsonObject json = new JsonObject();
		
		for(Entry<String, TreeSet<String>> entry : WurstClient.INSTANCE.keybinds
			.entrySet())
		{
			JsonArray commands = new JsonArray();
			
			entry.getValue().forEach((c) -> commands.add(new JsonPrimitive(c)));
			
			json.add(entry.getKey(), commands);
		}
		
		return json;
	}
}
