/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.options.keybinds;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeSet;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.wurstclient.WurstClient;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.options.KeybindManager;

public class GuiKeybindManager extends GuiScreen
{
	private GuiScreen prevMenu;
	public static GuiKeybindList bindList;
	
	public GuiKeybindManager(GuiScreen par1GuiScreen)
	{
		prevMenu = par1GuiScreen;
	}
	
	@Override
	public void initGui()
	{
		bindList = new GuiKeybindList(mc, this);
		bindList.registerScrollButtons(7, 8);
		bindList.elementClicked(-1, false, 0, 0);
		buttonList.clear();
		buttonList.add(
			new GuiButton(0, width / 2 - 102, height - 52, 100, 20, "Add"));
		buttonList
			.add(new GuiButton(1, width / 2 + 2, height - 52, 100, 20, "Edit"));
		buttonList.add(
			new GuiButton(2, width / 2 - 102, height - 28, 100, 20, "Remove"));
		buttonList
			.add(new GuiButton(3, width / 2 + 2, height - 28, 100, 20, "Back"));
		buttonList.add(new GuiButton(4, 8, 8, 100, 20, "Reset Keybinds"));
		WurstClient.INSTANCE.analytics.trackPageView("/options/keybind-manager",
			"Keybind Manager");
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		buttonList.get(1).enabled = bindList.getSelectedSlot() != -1;
		buttonList.get(2).enabled = bindList.getSelectedSlot() != -1;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void actionPerformed(GuiButton clickedButton)
	{
		if(clickedButton.enabled)
			if(clickedButton.id == 0)
				mc.displayGuiScreen(new GuiKeybindChange(this, null));
			else if(clickedButton.id == 3)
				mc.displayGuiScreen(prevMenu);
			else if(clickedButton.id == 4)
				mc.displayGuiScreen(new GuiYesNo(this,
					"Are you sure you want to reset your keybinds?",
					"This cannot be undone!", 0));
			else
			{
				if(bindList.getSelectedSlot() > WurstClient.INSTANCE.keybinds
					.size())
					bindList.elementClicked(
						WurstClient.INSTANCE.keybinds.size(), false, 0, 0);
				if(clickedButton.id == 1)
				{
					Entry<String, TreeSet<String>> entry =
						WurstClient.INSTANCE.keybinds.entrySet()
							.toArray(new Entry[WurstClient.INSTANCE.keybinds
								.size()])[bindList.getSelectedSlot()];
					mc.displayGuiScreen(new GuiKeybindChange(this, entry));
				}else if(clickedButton.id == 2)
				{
					Entry<String, String> entry =
						WurstClient.INSTANCE.keybinds.entrySet()
							.toArray(new Entry[WurstClient.INSTANCE.keybinds
								.size()])[bindList.getSelectedSlot()];
					WurstClient.INSTANCE.keybinds.remove(entry.getKey());
					ConfigFiles.KEYBINDS.save();
					WurstClient.INSTANCE.analytics.trackEvent("keybinds",
						"remove", entry.getKey());
				}
			}
	}
	
	@Override
	public void confirmClicked(boolean par1, int par2)
	{
		if(par1)
		{
			WurstClient.INSTANCE.keybinds = new KeybindManager();
			ConfigFiles.KEYBINDS.save();
			WurstClient.INSTANCE.analytics.trackEvent("keybinds", "reset");
		}
		mc.displayGuiScreen(this);
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		if(par2 == 28 || par2 == 156)
			actionPerformed(buttonList.get(0));
	}
	
	/**
	 * Called when the mouse is clicked.
	 *
	 * @throws IOException
	 */
	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException
	{
		if(par2 >= 36 && par2 <= height - 57)
			if(par1 >= width / 2 + 140 || par1 <= width / 2 - 126)
				bindList.elementClicked(-1, false, 0, 0);
		super.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		bindList.handleMouseInput();
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		bindList.drawScreen(par1, par2, par3);
		drawCenteredString(fontRendererObj, "Keybind Manager", width / 2, 8,
			16777215);
		drawCenteredString(fontRendererObj,
			"Keybinds: " + WurstClient.INSTANCE.keybinds.size(), width / 2, 20,
			16777215);
		super.drawScreen(par1, par2, par3);
	}
}
