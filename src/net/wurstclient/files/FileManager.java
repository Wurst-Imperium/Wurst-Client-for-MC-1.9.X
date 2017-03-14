/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
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
import java.util.TreeSet;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.wurstclient.WurstClient;
import net.wurstclient.alts.Alt;
import net.wurstclient.alts.Encryption;
import net.wurstclient.gui.alts.GuiAltList;
import net.wurstclient.mods.*;
import net.wurstclient.mods.Mod.Category;
import net.wurstclient.navigator.Navigator;
import net.wurstclient.navigator.NavigatorItem;
import net.wurstclient.navigator.settings.NavigatorSetting;
import net.wurstclient.options.FriendsList;
import net.wurstclient.options.OptionsManager;
import net.wurstclient.utils.JsonUtils;
import net.wurstclient.utils.XRayUtils;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class FileManager
{
	public final File wurstDir = new File(Minecraft.getMinecraft().mcDataDir,
		"wurst");
	public final File autobuildDir = new File(wurstDir, "autobuild");
	public final File skinDir = new File(wurstDir, "skins");
	public final File serverlistsDir = new File(wurstDir, "serverlists");
	public final File spamDir = new File(wurstDir, "spam");
	public final File scriptsDir = new File(spamDir, "autorun");
	
	public final File alts = new File(wurstDir, "alts.json");
	public final File friends = new File(wurstDir, "friends.json");
	public final File modules = new File(wurstDir, "modules.json");
	public final File navigatorData = new File(wurstDir, "navigator.json");
	public final File keybinds = new File(wurstDir, "keybinds.json");
	public final File options = new File(wurstDir, "options.json");
	public final File autoMaximize = new File(
		Minecraft.getMinecraft().mcDataDir + "/wurst/automaximize.json");
	public final File xray = new File(wurstDir, "xray.json");
	
	public void init()
	{
		if(!wurstDir.exists())
			wurstDir.mkdir();
		if(!autobuildDir.exists())
			autobuildDir.mkdir();
		if(!spamDir.exists())
			spamDir.mkdir();
		if(!scriptsDir.exists())
			scriptsDir.mkdir();
		if(!skinDir.exists())
			skinDir.mkdir();
		if(!serverlistsDir.exists())
			serverlistsDir.mkdir();
		if(!options.exists())
			saveOptions();
		else
			loadOptions();
		if(!modules.exists())
			saveMods();
		else
			loadMods();
		if(!keybinds.exists())
			saveKeybinds();
		else
			loadKeybinds();
		if(!navigatorData.exists())
			saveNavigatorData();
		else
			loadNavigatorData();
		if(!alts.exists())
			saveAlts();
		else
			loadAlts();
		if(!friends.exists())
			saveFriends();
		else
			loadFriends();
		if(!xray.exists())
		{
			XRayUtils.initXRayBlocks();
			saveXRayBlocks();
		}else
			loadXRayBlocks();
		File[] autobuildFiles = autobuildDir.listFiles();
		if(autobuildFiles != null && autobuildFiles.length == 0)
			createDefaultAutoBuildTemplates();
		loadAutoBuildTemplates();
		AutoBuildMod autoBuildMod = WurstClient.INSTANCE.mods.autoBuildMod;
		autoBuildMod.initTemplateSetting();
		if(autoBuildMod.getTemplate() >= AutoBuildMod.names.size())
		{
			autoBuildMod.setTemplate(0);
			saveNavigatorData();
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
	
	private HashSet<String> modBlacklist = Sets.newHashSet(
		AntiAfkMod.class.getName(), BlinkMod.class.getName(),
		ArenaBrawlMod.class.getName(), AutoBuildMod.class.getName(),
		AutoSignMod.class.getName(), FightBotMod.class.getName(),
		FollowMod.class.getName(), ForceOpMod.class.getName(),
		FreecamMod.class.getName(), InvisibilityMod.class.getName(),
		LsdMod.class.getName(), MassTpaMod.class.getName(),
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
				if(mod != null && mod.getCategory() != Category.HIDDEN
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
	
	public void saveKeybinds()
	{
		try
		{
			JsonObject json = new JsonObject();
			Iterator<Entry<String, TreeSet<String>>> itr =
				WurstClient.INSTANCE.keybinds.entrySet().iterator();
			while(itr.hasNext())
			{
				Entry<String, TreeSet<String>> entry = itr.next();
				JsonArray jsonCmds = new JsonArray();
				entry.getValue().forEach(
					(cmd) -> jsonCmds.add(new JsonPrimitive(cmd)));
				json.add(entry.getKey(), jsonCmds);
			}
			PrintWriter save = new PrintWriter(new FileWriter(keybinds));
			save.println(JsonUtils.prettyGson.toJson(json));
			save.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadKeybinds()
	{
		try
		{
			// load file
			BufferedReader load = new BufferedReader(new FileReader(keybinds));
			JsonObject json = (JsonObject)JsonUtils.jsonParser.parse(load);
			load.close();
			
			// clear keybinds
			WurstClient.INSTANCE.keybinds.clear();
			
			// add keybinds
			Iterator<Entry<String, JsonElement>> itr =
				json.entrySet().iterator();
			boolean needsUpdate = false;
			while(itr.hasNext())
			{
				Entry<String, JsonElement> entry = itr.next();
				
				if(entry.getValue().isJsonArray())
				{
					TreeSet<String> commmands = new TreeSet<>();
					entry.getValue().getAsJsonArray()
						.forEach((cmd) -> commmands.add(cmd.getAsString()));
					WurstClient.INSTANCE.keybinds
						.put(entry.getKey(), commmands);
					
				}else
				{
					String command = entry.getValue().getAsString();
					if(command.equalsIgnoreCase(".t clickgui"))
					{
						command = ".t navigator";
						needsUpdate = true;
					}
					
					WurstClient.INSTANCE.keybinds.put(entry.getKey(), command);
				}
			}
			
			// update file
			if(needsUpdate)
				WurstClient.INSTANCE.files.saveKeybinds();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveNavigatorData()
	{
		try
		{
			JsonObject json = new JsonObject();
			
			Navigator navigator = WurstClient.INSTANCE.navigator;
			navigator.forEach(new Consumer<NavigatorItem>()
			{
				@Override
				public void accept(NavigatorItem item)
				{
					JsonObject jsonFeature = new JsonObject();
					
					long preference = navigator.getPreference(item.getName());
					if(preference != 0L)
						jsonFeature.addProperty("preference", preference);
					
					if(!item.getSettings().isEmpty())
					{
						JsonObject jsonSettings = new JsonObject();
						for(NavigatorSetting setting : item.getSettings())
							try
							{
								setting.save(jsonSettings);
							}catch(Exception e)
							{
								e.printStackTrace();
							}
						jsonFeature.add("settings", jsonSettings);
					}
					
					if(!jsonFeature.entrySet().isEmpty())
						json.add(item.getName(), jsonFeature);
				}
			});
			
			PrintWriter save = new PrintWriter(new FileWriter(navigatorData));
			save.println(JsonUtils.prettyGson.toJson(json));
			save.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadNavigatorData()
	{
		try
		{
			BufferedReader load =
				new BufferedReader(new FileReader(navigatorData));
			JsonObject json = (JsonObject)JsonUtils.jsonParser.parse(load);
			load.close();
			
			Navigator navigator = WurstClient.INSTANCE.navigator;
			navigator.forEach(new Consumer<NavigatorItem>()
			{
				@Override
				public void accept(NavigatorItem item)
				{
					String itemName = item.getName();
					if(!json.has(itemName))
						return;
					JsonObject jsonFeature =
						json.get(itemName).getAsJsonObject();
					
					if(jsonFeature.has("preference"))
						navigator.setPreference(itemName,
							jsonFeature.get("preference").getAsLong());
					
					if(jsonFeature.has("settings"))
					{
						JsonObject jsonSettings =
							jsonFeature.get("settings").getAsJsonObject();
						for(NavigatorSetting setting : item.getSettings())
							try
							{
								setting.load(jsonSettings);
							}catch(Exception e)
							{
								e.printStackTrace();
							}
					}
				}
			});
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveOptions()
	{
		try
		{
			PrintWriter save = new PrintWriter(new FileWriter(options));
			save.println(JsonUtils.prettyGson
				.toJson(WurstClient.INSTANCE.options));
			save.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadOptions()
	{
		try
		{
			BufferedReader load = new BufferedReader(new FileReader(options));
			WurstClient.INSTANCE.options =
				JsonUtils.gson.fromJson(load, OptionsManager.class);
			load.close();
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
			autoMaximizeEnabled =
				JsonUtils.gson.fromJson(load, Boolean.class)
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
			Files.write(
				alts.toPath(),
				Encryption.encrypt(JsonUtils.prettyGson.toJson(json)).getBytes(
					Encryption.CHARSET));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadAlts()
	{
		try
		{
			JsonObject json =
				(JsonObject)JsonUtils.jsonParser.parse(Encryption
					.decrypt(new String(Files.readAllBytes(alts.toPath()),
						Encryption.CHARSET)));
			GuiAltList.alts.clear();
			Iterator<Entry<String, JsonElement>> itr =
				json.entrySet().iterator();
			while(itr.hasNext())
			{
				Entry<String, JsonElement> entry = itr.next();
				JsonObject jsonAlt = entry.getValue().getAsJsonObject();
				
				String email = entry.getKey();
				String password =
					jsonAlt.get("password") == null ? "" : jsonAlt.get(
						"password").getAsString();
				String name =
					jsonAlt.get("name") == null ? "" : jsonAlt.get("name")
						.getAsString();
				boolean starred =
					jsonAlt.get("starred") == null ? false : jsonAlt.get(
						"starred").getAsBoolean();
				
				GuiAltList.alts.add(new Alt(email, password, name, starred));
			}
			GuiAltList.sortAlts();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveFriends()
	{
		try
		{
			PrintWriter save = new PrintWriter(new FileWriter(friends));
			save.println(JsonUtils.prettyGson
				.toJson(WurstClient.INSTANCE.friends));
			save.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadFriends()
	{
		try
		{
			BufferedReader load = new BufferedReader(new FileReader(friends));
			WurstClient.INSTANCE.friends =
				JsonUtils.gson.fromJson(load, FriendsList.class);
			load.close();
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
				json.add(JsonUtils.prettyGson.toJsonTree(Block
					.getIdFromBlock(XRayMod.xrayBlocks.get(i))));
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
				{
					"Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.",
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
				json.add("blocks", JsonUtils.prettyGson.toJsonTree(
					entry.getValue(), int[][].class));
				PrintWriter save =
					new PrintWriter(new FileWriter(new File(autobuildDir,
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
			File[] files = autobuildDir.listFiles();
			if(files == null)
				return;
			for(File file : files)
			{
				BufferedReader load = new BufferedReader(new FileReader(file));
				JsonObject json = (JsonObject)JsonUtils.jsonParser.parse(load);
				load.close();
				AutoBuildMod.templates.add(JsonUtils.gson.fromJson(
					json.get("blocks"), int[][].class));
				AutoBuildMod.names.add(file.getName().substring(0,
					file.getName().indexOf(".json")));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
