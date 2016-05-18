/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client;

import org.darkstorm.minecraft.gui.theme.wurst.WurstTheme;

import tk.wurst_client.analytics.AnalyticsManager;
import tk.wurst_client.chat.ChatManager;
import tk.wurst_client.commands.CmdManager;
import tk.wurst_client.events.EventManager;
import tk.wurst_client.files.FileManager;
import tk.wurst_client.font.Fonts;
import tk.wurst_client.gui.GuiManager;
import tk.wurst_client.hooks.FrameHook;
import tk.wurst_client.mods.ModManager;
import tk.wurst_client.navigator.Navigator;
import tk.wurst_client.options.FriendsList;
import tk.wurst_client.options.KeybindManager;
import tk.wurst_client.options.OptionsManager;
import tk.wurst_client.special.SpfManager;
import tk.wurst_client.update.Updater;

public enum WurstClient
{
	INSTANCE;
	
	public static final String VERSION = "3.2";
	public boolean startupMessageDisabled = false;
	
	public AnalyticsManager analytics;
	public ChatManager chat;
	public CmdManager commands;
	public EventManager events;
	public FileManager files;
	public FriendsList friends;
	public GuiManager gui;
	public ModManager mods;
	public Navigator navigator;
	public KeybindManager keybinds;
	public OptionsManager options;
	public SpfManager special;
	public Updater updater;
	
	public void startClient()
	{
		events = new EventManager();
		mods = new ModManager();
		gui = new GuiManager();
		commands = new CmdManager();
		special = new SpfManager();
		files = new FileManager();
		updater = new Updater();
		chat = new ChatManager();
		keybinds = new KeybindManager();
		options = new OptionsManager();
		friends = new FriendsList();
		navigator = new Navigator();
		
		files.init();
		navigator.sortFeatures();
		Fonts.loadFonts();
		gui.setTheme(new WurstTheme());
		gui.setup();
		updater.checkForUpdate();
		analytics =
			new AnalyticsManager("UA-52838431-5", "client.wurst-client.tk");
		files.saveOptions();
		
		FrameHook.maximize();
	}
}
