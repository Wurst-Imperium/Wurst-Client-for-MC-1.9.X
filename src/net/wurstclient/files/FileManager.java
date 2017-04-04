/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.wurstclient.WurstClient;
import net.wurstclient.features.mods.AutoBuildMod;
import net.wurstclient.utils.JsonUtils;

public class FileManager
{
	public final File autoMaximize = new File(
		Minecraft.getMinecraft().mcDataDir + "/wurst/automaximize.json");
	
	public void init()
	{
		File[] autobuildFiles = WurstFolders.AUTOBUILD.toFile().listFiles();
		if(autobuildFiles != null && autobuildFiles.length == 0)
			createDefaultAutoBuildTemplates();
		loadAutoBuildTemplates();
		AutoBuildMod autoBuildMod = WurstClient.INSTANCE.mods.autoBuildMod;
		autoBuildMod.initTemplateSetting();
		if(autoBuildMod.getTemplate() >= AutoBuildMod.names.size())
		{
			autoBuildMod.setTemplate(0);
			ConfigFiles.NAVIGATOR.save();
		}
	}
	
	public boolean loadAutoMaximize()
	{
		boolean autoMaximizeEnabled = false;
		if(!autoMaximize.exists())
			saveAutoMaximize(true);
		try
		{
			BufferedReader load =
				new BufferedReader(new FileReader(autoMaximize));
			autoMaximizeEnabled = JsonUtils.gson.fromJson(load, Boolean.class)
				&& !Minecraft.isRunningOnMac;
			load.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return autoMaximizeEnabled;
	}
	
	public void saveAutoMaximize(boolean autoMaximizeEnabled)
	{
		try
		{
			if(!autoMaximize.getParentFile().exists())
				autoMaximize.getParentFile().mkdirs();
			PrintWriter save = new PrintWriter(new FileWriter(autoMaximize));
			save.println(JsonUtils.prettyGson.toJson(autoMaximizeEnabled));
			save.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void createDefaultAutoBuildTemplates()
	{
		try
		{
			String[] comment =
				{"Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.",
					"This Source Code Form is subject to the terms of the Mozilla Public",
					"License, v. 2.0. If a copy of the MPL was not distributed with this",
					"file, You can obtain one at http://mozilla.org/MPL/2.0/."};
			Iterator<Entry<String, int[][]>> itr =
				new DefaultAutoBuildTemplates().entrySet().iterator();
			while(itr.hasNext())
			{
				Entry<String, int[][]> entry = itr.next();
				JsonObject json = new JsonObject();
				json.add("__comment",
					JsonUtils.prettyGson.toJsonTree(comment, String[].class));
				json.add("blocks", JsonUtils.prettyGson
					.toJsonTree(entry.getValue(), int[][].class));
				PrintWriter save = new PrintWriter(
					new FileWriter(new File(WurstFolders.AUTOBUILD.toFile(),
						entry.getKey() + ".json")));
				save.println(JsonUtils.prettyGson.toJson(json));
				save.close();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadAutoBuildTemplates()
	{
		try
		{
			File[] files = WurstFolders.AUTOBUILD.toFile().listFiles();
			if(files == null)
				return;
			for(File file : files)
			{
				BufferedReader load = new BufferedReader(new FileReader(file));
				JsonObject json = (JsonObject)JsonUtils.jsonParser.parse(load);
				load.close();
				AutoBuildMod.templates.add(
					JsonUtils.gson.fromJson(json.get("blocks"), int[][].class));
				AutoBuildMod.names.add(file.getName().substring(0,
					file.getName().indexOf(".json")));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
