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
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.wurstclient.WurstClient;
import net.wurstclient.alts.Alt;
import net.wurstclient.alts.Encryption;
import net.wurstclient.features.mods.*;
import net.wurstclient.gui.alts.GuiAltList;
import net.wurstclient.utils.JsonUtils;
import net.wurstclient.utils.XRayUtils;

public class FileManager
{
	public final File alts = new File(WurstFolders.MAIN.toFile(), "alts.json");
	public final File modules =
		new File(WurstFolders.MAIN.toFile(), "modules.json");
	public final File autoMaximize = new File(
		Minecraft.getMinecraft().mcDataDir + "/wurst/automaximize.json");
	public final File xray = new File(WurstFolders.MAIN.toFile(), "xray.json");
	
	public void init()
	{
		if(!modules.exists())
			saveMods();
		else
			loadMods();
		if(!alts.exists())
			saveAlts();
		else
			loadAlts();
		if(!xray.exists())
		{
			XRayUtils.initXRayBlocks();
			saveXRayBlocks();
		}else
			loadXRayBlocks();
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
	
	public void saveMods()
	{
		try
		{
			JsonObject json = new JsonObject();
			for(Mod mod : WurstClient.INSTANCE.mods.getAllMods())
			{
				JsonObject jsonMod = new JsonObject();
				jsonMod.addProperty("enabled", mod.isEnabled());
				json.add(mod.getName(), jsonMod);
			}
			PrintWriter save = new PrintWriter(new FileWriter(modules));
			save.println(JsonUtils.prettyGson.toJson(json));
			save.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private HashSet<String> modBlacklist =
		Sets.newHashSet(AntiAfkMod.class.getName(), BlinkMod.class.getName(),
			AutoBuildMod.class.getName(), AutoSignMod.class.getName(),
			FightBotMod.class.getName(), FollowMod.class.getName(),
			ForceOpMod.class.getName(), FreecamMod.class.getName(),
			InvisibilityMod.class.getName(), LsdMod.class.getName(),
			MassTpaMod.class.getName(), NavigatorMod.class.getName(),
			ProtectMod.class.getName(), RemoteViewMod.class.getName(),
			SpammerMod.class.getName());
	
	public boolean isModBlacklisted(Mod mod)
	{
		return modBlacklist.contains(mod.getClass().getName());
	}
	
	public void loadMods()
	{
		try
		{
			BufferedReader load = new BufferedReader(new FileReader(modules));
			JsonObject json = (JsonObject)JsonUtils.jsonParser.parse(load);
			load.close();
			Iterator<Entry<String, JsonElement>> itr =
				json.entrySet().iterator();
			while(itr.hasNext())
			{
				Entry<String, JsonElement> entry = itr.next();
				Mod mod =
					WurstClient.INSTANCE.mods.getModByName(entry.getKey());
				if(mod != null
					&& !modBlacklist.contains(mod.getClass().getName()))
				{
					JsonObject jsonModule = (JsonObject)entry.getValue();
					boolean enabled = jsonModule.get("enabled").getAsBoolean();
					if(enabled)
						mod.enableOnStartup();
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
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
	
	public void saveAlts()
	{
		try
		{
			JsonObject json = new JsonObject();
			for(Alt alt : GuiAltList.alts)
			{
				JsonObject jsonAlt = new JsonObject();
				jsonAlt.addProperty("password", alt.getPassword());
				jsonAlt.addProperty("name", alt.getName());
				jsonAlt.addProperty("starred", alt.isStarred());
				json.add(alt.getEmail(), jsonAlt);
			}
			Files.write(alts.toPath(),
				Encryption.encrypt(JsonUtils.prettyGson.toJson(json))
					.getBytes(Encryption.CHARSET));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadAlts()
	{
		try
		{
			JsonObject json = (JsonObject)JsonUtils.jsonParser.parse(
				Encryption.decrypt(new String(Files.readAllBytes(alts.toPath()),
					Encryption.CHARSET)));
			GuiAltList.alts.clear();
			Iterator<Entry<String, JsonElement>> itr =
				json.entrySet().iterator();
			while(itr.hasNext())
			{
				Entry<String, JsonElement> entry = itr.next();
				JsonObject jsonAlt = entry.getValue().getAsJsonObject();
				
				String email = entry.getKey();
				String password = jsonAlt.get("password") == null ? ""
					: jsonAlt.get("password").getAsString();
				String name = jsonAlt.get("name") == null ? ""
					: jsonAlt.get("name").getAsString();
				boolean starred = jsonAlt.get("starred") == null ? false
					: jsonAlt.get("starred").getAsBoolean();
				
				GuiAltList.alts.add(new Alt(email, password, name, starred));
			}
			GuiAltList.sortAlts();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveXRayBlocks()
	{
		try
		{
			XRayUtils.sortBlocks();
			JsonArray json = new JsonArray();
			for(int i = 0; i < XRayMod.xrayBlocks.size(); i++)
				json.add(JsonUtils.prettyGson.toJsonTree(
					Block.getIdFromBlock(XRayMod.xrayBlocks.get(i))));
			PrintWriter save = new PrintWriter(new FileWriter(xray));
			save.println(JsonUtils.prettyGson.toJson(json));
			save.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadXRayBlocks()
	{
		try
		{
			BufferedReader load = new BufferedReader(new FileReader(xray));
			JsonArray json = JsonUtils.jsonParser.parse(load).getAsJsonArray();
			load.close();
			Iterator<JsonElement> itr = json.iterator();
			while(itr.hasNext())
				try
				{
					String jsonBlock = itr.next().getAsString();
					XRayMod.xrayBlocks.add(Block.getBlockFromName(jsonBlock));
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			XRayUtils.sortBlocks();
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
