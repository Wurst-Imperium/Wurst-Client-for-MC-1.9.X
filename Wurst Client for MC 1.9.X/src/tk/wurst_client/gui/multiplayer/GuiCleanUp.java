/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.multiplayer;

import java.io.IOException;
import java.util.Arrays;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.TextFormatting;

import org.lwjgl.input.Keyboard;

import tk.wurst_client.WurstClient;

public class GuiCleanUp extends GuiScreen
{
	private GuiMultiplayer prevMenu;
	private boolean removeAll;
	private String[] toolTips =
		{
			"",
			"Start the Clean Up with the settings\n" + "you specified above.\n"
				+ "It might look like the game is not\n"
				+ "reacting for a couple of seconds.",
			"Servers that clearly don't exist.",
			"Servers that run a different Minecraft\n" + "version than you.",
			"All servers that failed the last ping.\n"
				+ "Make sure that the last ping is complete\n"
				+ "before you do this. That means: Go back,\n"
				+ "press the refresh button and wait until\n"
				+ "all servers are done refreshing.",
			"All servers where the name starts with \"Grief me\"\n"
				+ "Useful for removing servers found by ServerFinder.",
			"This will completely clear your server\n"
				+ "list. §cUse with caution!§r",
			"Renames your servers to \"Grief me #1\",\n"
				+ "\"Grief me #2\", etc.",};
	
	public GuiCleanUp(GuiMultiplayer prevMultiplayerMenu)
	{
		prevMenu = prevMultiplayerMenu;
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{	
		
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 144 + 12,
			"Cancel"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 168 + 12,
			"Clean Up"));
		buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 - 24 + 12,
			"Unknown Hosts: "
				+ removeOrKeep(WurstClient.INSTANCE.options.cleanupUnknown)));
		buttonList.add(new GuiButton(3, width / 2 - 100, height / 4 + 0 + 12,
			"Outdated Servers: "
				+ removeOrKeep(WurstClient.INSTANCE.options.cleanupOutdated)));
		buttonList.add(new GuiButton(4, width / 2 - 100, height / 4 + 24 + 12,
			"Failed Ping: "
				+ removeOrKeep(WurstClient.INSTANCE.options.cleanupFailed)));
		buttonList.add(new GuiButton(5, width / 2 - 100, height / 4 + 48 + 12,
			"\"Grief me\" Servers: "
				+ removeOrKeep(WurstClient.INSTANCE.options.cleanupGriefMe)));
		buttonList.add(new GuiButton(6, width / 2 - 100, height / 4 + 72 + 12,
			"§cRemove all Servers: " + yesOrNo(removeAll)));
		buttonList.add(new GuiButton(7, width / 2 - 100, height / 4 + 96 + 12,
			"Rename all Servers: "
				+ yesOrNo(WurstClient.INSTANCE.options.cleanupRename)));
		WurstClient.INSTANCE.analytics.trackPageView("/multiplayer/clean-up",
			"Clean Up");
	}
	
	private String yesOrNo(boolean bool)
	{
		return bool ? "Yes" : "No";
	}
	
	private String removeOrKeep(boolean bool)
	{
		return bool ? "Remove" : "Keep";
	}
	
	/**
	 * "Called when the screen is unloaded. Used to disable keyboard repeat events."
	 */
	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	protected void actionPerformed(GuiButton clickedButton)
	{
		if(clickedButton.enabled)
			if(clickedButton.id == 0)
				mc.displayGuiScreen(prevMenu);
			else if(clickedButton.id == 1)
			{// Clean Up
				WurstClient.INSTANCE.analytics.trackEvent("clean up", "start");
				if(removeAll)
				{
					prevMenu.savedServerList.clearServerList();
					prevMenu.savedServerList.saveServerList();
					prevMenu.serverListSelector.setSelectedSlotIndex(-1);
					prevMenu.serverListSelector
						.func_148195_a(prevMenu.savedServerList);
					mc.displayGuiScreen(prevMenu);
					return;
				}
				for(int i = prevMenu.savedServerList.countServers() - 1; i >= 0; i--)
				{
					ServerData server =
						prevMenu.savedServerList.getServerData(i);
					if(WurstClient.INSTANCE.options.cleanupUnknown
						&& server.serverMOTD.equals(TextFormatting.DARK_RED
							+ "Can\'t resolve hostname")
						|| WurstClient.INSTANCE.options.cleanupOutdated
						&& server.version != 47
						|| WurstClient.INSTANCE.options.cleanupFailed
						&& server.pingToServer != -2L
						&& server.pingToServer < 0L
						|| WurstClient.INSTANCE.options.cleanupGriefMe
						&& server.serverName.startsWith("Grief me"))
					{
						prevMenu.savedServerList.removeServerData(i);
						prevMenu.savedServerList.saveServerList();
						prevMenu.serverListSelector.setSelectedSlotIndex(-1);
						prevMenu.serverListSelector
							.func_148195_a(prevMenu.savedServerList);
					}
				}
				if(WurstClient.INSTANCE.options.cleanupRename)
					for(int i = 0; i < prevMenu.savedServerList.countServers(); i++)
					{
						ServerData server =
							prevMenu.savedServerList.getServerData(i);
						server.serverName = "Grief me #" + (i + 1);
						prevMenu.savedServerList.saveServerList();
						prevMenu.serverListSelector.setSelectedSlotIndex(-1);
						prevMenu.serverListSelector
							.func_148195_a(prevMenu.savedServerList);
					}
				mc.displayGuiScreen(prevMenu);
			}else if(clickedButton.id == 2)
			{// Unknown host
				WurstClient.INSTANCE.options.cleanupUnknown =
					!WurstClient.INSTANCE.options.cleanupUnknown;
				clickedButton.displayString =
					"Unknown Hosts: "
						+ removeOrKeep(WurstClient.INSTANCE.options.cleanupUnknown);
				WurstClient.INSTANCE.files.saveOptions();
				WurstClient.INSTANCE.analytics.trackEvent("clean up",
					"unknown host",
					removeOrKeep(WurstClient.INSTANCE.options.cleanupUnknown));
			}else if(clickedButton.id == 3)
			{// Outdated
				WurstClient.INSTANCE.options.cleanupOutdated =
					!WurstClient.INSTANCE.options.cleanupOutdated;
				clickedButton.displayString =
					"Outdated Servers: "
						+ removeOrKeep(WurstClient.INSTANCE.options.cleanupOutdated);
				WurstClient.INSTANCE.files.saveOptions();
				WurstClient.INSTANCE.analytics.trackEvent("clean up",
					"outdated servers",
					removeOrKeep(WurstClient.INSTANCE.options.cleanupOutdated));
			}else if(clickedButton.id == 4)
			{// Failed ping
				WurstClient.INSTANCE.options.cleanupFailed =
					!WurstClient.INSTANCE.options.cleanupFailed;
				clickedButton.displayString =
					"Failed Ping: "
						+ removeOrKeep(WurstClient.INSTANCE.options.cleanupFailed);
				WurstClient.INSTANCE.files.saveOptions();
				WurstClient.INSTANCE.analytics.trackEvent("clean up",
					"failed ping",
					removeOrKeep(WurstClient.INSTANCE.options.cleanupFailed));
			}else if(clickedButton.id == 5)
			{// Grief me
				WurstClient.INSTANCE.options.cleanupGriefMe =
					!WurstClient.INSTANCE.options.cleanupGriefMe;
				WurstClient.INSTANCE.files.saveOptions();
				clickedButton.displayString =
					"\"Grief Me\" Servers: "
						+ removeOrKeep(WurstClient.INSTANCE.options.cleanupGriefMe);
				WurstClient.INSTANCE.analytics.trackEvent("clean up",
					"grief me",
					removeOrKeep(WurstClient.INSTANCE.options.cleanupGriefMe));
			}else if(clickedButton.id == 6)
			{// Remove
				removeAll = !removeAll;
				clickedButton.displayString =
					"§cRemove all Servers: " + yesOrNo(removeAll);
				WurstClient.INSTANCE.analytics.trackEvent("clean up",
					"remove all servers", yesOrNo(removeAll));
			}else if(clickedButton.id == 7)
			{// Rename
				WurstClient.INSTANCE.options.cleanupRename =
					!WurstClient.INSTANCE.options.cleanupRename;
				clickedButton.displayString =
					"Rename all Servers: "
						+ yesOrNo(WurstClient.INSTANCE.options.cleanupRename);
				WurstClient.INSTANCE.files.saveOptions();
				WurstClient.INSTANCE.analytics.trackEvent("clean up",
					"rename servers",
					yesOrNo(WurstClient.INSTANCE.options.cleanupRename));
			}
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		if(par2 == 28 || par2 == 156)
			actionPerformed((GuiButton)buttonList.get(0));
	}
	
	/**
	 * Called when the mouse is clicked.
	 *
	 * @throws IOException
	 */
	@Override
	protected void mouseClicked(int par1, int par2, int par3)
		throws IOException
	{
		super.mouseClicked(par1, par2, par3);
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "Clean Up", width / 2, 20, 16777215);
		drawCenteredString(fontRendererObj,
			"Please select the servers you want to remove:", width / 2, 36,
			10526880);
		super.drawScreen(par1, par2, par3);
		for(int i = 0; i < buttonList.size(); i++)
		{
			GuiButton button = (GuiButton)buttonList.get(i);
			if(button.isMouseOver() && !toolTips[button.id].isEmpty())
			{
				drawHoveringText(
					Arrays.asList(toolTips[button.id].split("\n")), par1, par2);
				break;
			}
		}
	}
}
