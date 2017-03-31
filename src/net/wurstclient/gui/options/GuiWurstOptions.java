/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.options;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.WurstClient;
import net.wurstclient.gui.options.keybinds.GuiKeybindManager;
import net.wurstclient.gui.options.xray.GuiXRayBlocksManager;
import net.wurstclient.gui.options.zoom.GuiZoomManager;
import net.wurstclient.options.OptionsManager.GoogleAnalytics;
import net.wurstclient.utils.MiscUtils;

public class GuiWurstOptions extends GuiScreen
{
	private GuiScreen prevMenu;
	private String[] modListModes = {"Auto", "Count", "Hidden"};
	private String[] toolTips = {"",
		"Add/remove friends by clicking them with\n"
			+ "the middle mouse button.",
		"How the mod list under the Wurst logo\n" + "should be displayed.\n"
			+ "§lModes:§r\n" + "§nAuto§r: Renders the whole list if it fits\n"
			+ "onto the screen.\n"
			+ "§nCount§r: Only renders the number of active\n" + "mods.\n"
			+ "§nHidden§r: Renders nothing.",
		"Automatically maximizes the Minecraft window.\n"
			+ "Windows & Linux only!",
		"Whether or not the Wurst News should be\n" + "shown in the main menu.",
		"Sends anonymous usage statistics that\n"
			+ "help us improve the Wurst Client.",
		"Keybinds allow you to toggle any mod\n"
			+ "or command by simply pressing a\n" + "button.",
		"Manager for the blocks that X-Ray will\n" + "show.",
		"The Zoom Manager allows you to\n" + "change the zoom key, how far it\n"
			+ "will zoom in and more.",
		"", "",
		"The official website of the Wurst\n"
			+ "Client. Here you can find the\n"
			+ "latest Wurst updates, news and the\n" + "Wurst wiki.",
		"The official YouTube channel of the\n"
			+ "Wurst Client. Here we post Wurst\n"
			+ "update videos, Wurst tutorials and\n" + "more.",
		"Our Twitter account shows the latest\n"
			+ "Wurst updates, news and sneak peeks in\n"
			+ "140 characters or less.",
		"", ""};
	private boolean autoMaximize;
	
	public GuiWurstOptions(GuiScreen par1GuiScreen)
	{
		prevMenu = par1GuiScreen;
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		autoMaximize = WurstClient.INSTANCE.files.loadAutoMaximize();
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 144 - 16,
			200, 20, "Back"));
		buttonList.add(
			new GuiButton(1, width / 2 - 154, height / 4 + 24 - 16, 100, 20,
				"Click Friends: "
					+ (WurstClient.INSTANCE.options.middleClickFriends ? "ON"
						: "OFF")));
		buttonList.add(new GuiButton(2, width / 2 - 154, height / 4 + 48 - 16,
			100, 20, "Mod List: "
				+ modListModes[WurstClient.INSTANCE.options.modListMode]));
		buttonList.add(new GuiButton(3, width / 2 - 154, height / 4 + 72 - 16,
			100, 20, "AutoMaximize: " + (autoMaximize ? "ON" : "OFF")));
		buttonList.add(new GuiButton(4, width / 2 - 154, height / 4 + 96 - 16,
			100, 20, "Wurst News: "
				+ (WurstClient.INSTANCE.options.wurstNews ? "ON" : "OFF")));
		buttonList.add(
			new GuiButton(5, width / 2 - 154, height / 4 + 120 - 16, 100, 20,
				"Analytics: "
					+ (WurstClient.INSTANCE.options.google_analytics.enabled
						? "ON" : "OFF")));
		buttonList.add(new GuiButton(6, width / 2 - 50, height / 4 + 24 - 16,
			100, 20, "Keybinds"));
		buttonList.add(new GuiButton(7, width / 2 - 50, height / 4 + 48 - 16,
			100, 20, "X-Ray Blocks"));
		buttonList.add(new GuiButton(8, width / 2 - 50, height / 4 + 72 - 16,
			100, 20, "Zoom"));
		// this.buttonList.add(new GuiButton(9, this.width / 2 - 50, this.height
		// / 4 + 96 - 16, 100, 20, "???"));
		// this.buttonList.add(new GuiButton(10, this.width / 2 - 50,
		// this.height / 4 + 120 - 16, 100, 20, "???"));
		buttonList.add(new GuiButton(11, width / 2 + 54, height / 4 + 24 - 16,
			100, 20, "Wurst Website"));
		buttonList.add(new GuiButton(12, width / 2 + 54, height / 4 + 48 - 16,
			100, 20, "Wurst on YouTube"));
		buttonList.add(new GuiButton(13, width / 2 + 54, height / 4 + 72 - 16,
			100, 20, "Wurst on Twitter"));
		buttonList.add(new GuiButton(14, width / 2 + 54, height / 4 + 96 - 16,
			100, 20, "Report a Bug"));
		// buttonList.add(new GuiButton(15, width / 2 + 54, height / 4 + 120 -
		// 16, 100, 20, "???"));
		buttonList.get(3).enabled = !Minecraft.isRunningOnMac;
	}
	
	@Override
	protected void actionPerformed(GuiButton clickedButton)
	{
		if(clickedButton.enabled)
			if(clickedButton.id == 0)
				mc.displayGuiScreen(prevMenu);
			else if(clickedButton.id == 1)
			{// Click Friends
				WurstClient.INSTANCE.options.middleClickFriends =
					!WurstClient.INSTANCE.options.middleClickFriends;
				clickedButton.displayString = "Click Friends: "
					+ (WurstClient.INSTANCE.options.middleClickFriends ? "ON"
						: "OFF");
				WurstClient.INSTANCE.files.saveOptions();
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"click friends",
					WurstClient.INSTANCE.options.middleClickFriends ? "ON"
						: "OFF");
			}else if(clickedButton.id == 2)
			{// Mod List
				WurstClient.INSTANCE.options.modListMode++;
				if(WurstClient.INSTANCE.options.modListMode > 2)
					WurstClient.INSTANCE.options.modListMode = 0;
				clickedButton.displayString = "Mod List: "
					+ modListModes[WurstClient.INSTANCE.options.modListMode];
				WurstClient.INSTANCE.files.saveOptions();
				WurstClient.INSTANCE.analytics.trackEvent("options", "mod list",
					modListModes[WurstClient.INSTANCE.options.modListMode]);
			}else if(clickedButton.id == 3)
			{// AutoMaximize
				autoMaximize = !autoMaximize;
				clickedButton.displayString =
					"AutoMaximize: " + (autoMaximize ? "ON" : "OFF");
				WurstClient.INSTANCE.files.saveAutoMaximize(autoMaximize);
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"automaximize", autoMaximize ? "ON" : "OFF");
			}else if(clickedButton.id == 4)
			{// Wurst News
				WurstClient.INSTANCE.options.wurstNews =
					!WurstClient.INSTANCE.options.wurstNews;
				clickedButton.displayString = "Wurst News: "
					+ (WurstClient.INSTANCE.options.wurstNews ? "ON" : "OFF");
				WurstClient.INSTANCE.files.saveOptions();
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"wurst news",
					WurstClient.INSTANCE.options.wurstNews ? "ON" : "OFF");
			}else if(clickedButton.id == 5)
			{// Analytics
				GoogleAnalytics analytics =
					WurstClient.INSTANCE.options.google_analytics;
				if(analytics.enabled)
					WurstClient.INSTANCE.analytics.trackEvent("options",
						"analytics", "disable");
				analytics.enabled = !analytics.enabled;
				if(analytics.enabled)
					WurstClient.INSTANCE.analytics.trackEvent("options",
						"analytics", "enable");
				clickedButton.displayString =
					"Analytics: " + (analytics.enabled ? "ON" : "OFF");
				WurstClient.INSTANCE.files.saveOptions();
			}else if(clickedButton.id == 6)
				// Keybind Manager
				mc.displayGuiScreen(new GuiKeybindManager(this));
			else if(clickedButton.id == 7)
				// X-Ray Block Manager
				mc.displayGuiScreen(new GuiXRayBlocksManager(this));
			else if(clickedButton.id == 8)
				// Zoom Manager
				mc.displayGuiScreen(new GuiZoomManager(this));
			else if(clickedButton.id == 9)
			{
				
			}else if(clickedButton.id == 10)
			{
				
			}else if(clickedButton.id == 11)
			{
				MiscUtils.openLink("https://www.wurst-client.tk/");
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"wurst website");
			}else if(clickedButton.id == 12)
			{
				MiscUtils.openLink("https://www.wurst-client.tk/youtube/");
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"youtube channel");
			}else if(clickedButton.id == 13)
			{
				MiscUtils.openLink("https://www.wurst-client.tk/twitter/");
				WurstClient.INSTANCE.analytics.trackEvent("options", "twitter");
			}else if(clickedButton.id == 14)
			{
				MiscUtils.openLink("https://www.wurst-client.tk/bugs/");
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"bug tracker");
			}else if(clickedButton.id == 15)
			{
				
			}
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		super.updateScreen();
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "Wurst Options", width / 2, 40,
			0xffffff);
		drawCenteredString(fontRendererObj, "Settings", width / 2 - 104,
			height / 4 + 24 - 28, 0xcccccc);
		drawCenteredString(fontRendererObj, "Managers", width / 2,
			height / 4 + 24 - 28, 0xcccccc);
		drawCenteredString(fontRendererObj, "Online", width / 2 + 104,
			height / 4 + 24 - 28, 0xcccccc);
		super.drawScreen(par1, par2, par3);
		for(int i = 0; i < buttonList.size(); i++)
		{
			GuiButton button = buttonList.get(i);
			if(button.isMouseOver() && !toolTips[button.id].isEmpty())
			{
				drawHoveringText(Arrays.asList(toolTips[button.id].split("\n")),
					par1, par2);
				break;
			}
		}
	}
}
