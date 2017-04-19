/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.options.keybinds;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.WurstClient;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.gui.options.GuiPressAKey;
import net.wurstclient.gui.options.GuiPressAKeyCallback;

public class GuiKeybindChange extends GuiScreen implements GuiPressAKeyCallback
{
	private GuiScreen prevScreen;
	private GuiTextField commandBox;
	private Entry<String, TreeSet<String>> entry;
	private String key = "NONE";
	
	public GuiKeybindChange(GuiScreen prevScreen,
		Entry<String, TreeSet<String>> entry)
	{
		this.prevScreen = prevScreen;
		this.entry = entry;
		if(entry != null)
			key = entry.getKey();
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		commandBox.updateCursorCounter();
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.add(new GuiButton(0, width / 2 - 100, 60, "Change Key"));
		buttonList
			.add(new GuiButton(1, width / 2 - 100, height / 4 + 72, "Save"));
		buttonList
			.add(new GuiButton(2, width / 2 - 100, height / 4 + 96, "Cancel"));
		commandBox =
			new GuiTextField(0, fontRendererObj, width / 2 - 100, 100, 200, 20);
		commandBox.setMaxStringLength(65536);
		commandBox.setFocused(true);
		if(entry != null)
		{
			String cmds = "";
			for(String cmd : entry.getValue())
			{
				if(!cmds.isEmpty())
					cmds += ";";
				cmds += cmd;
			}
			commandBox.setText(cmds);
		}
	}
	
	/**
	 * "Called when the screen is unloaded. Used to disable keyboard repeat
	 * events."
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
				mc.displayGuiScreen(new GuiPressAKey(this));
			else if(clickedButton.id == 1)
			{
				if(entry != null)
					WurstClient.INSTANCE.keybinds.remove(entry.getKey());
				WurstClient.INSTANCE.keybinds.put(key, new TreeSet<>(
					Arrays.asList(commandBox.getText().split(";"))));
				ConfigFiles.KEYBINDS.save();
				mc.displayGuiScreen(prevScreen);
				WurstClient.INSTANCE.analytics.trackEvent("keybinds", "set",
					key);
			}else if(clickedButton.id == 2)
				mc.displayGuiScreen(prevScreen);
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		commandBox.textboxKeyTyped(par1, par2);
	}
	
	@Override
	public void setKey(String key)
	{
		this.key = key;
	}
	
	/**
	 * Called when the mouse is clicked.
	 *
	 * @throws IOException
	 */
	@Override
	protected void mouseClicked(int par1, int par2, int par3) throws IOException
	{
		super.mouseClicked(par1, par2, par3);
		commandBox.mouseClicked(par1, par2, par3);
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawBackground(0);
		drawCenteredString(fontRendererObj,
			(entry != null ? "Edit" : "Add") + " Keybind", width / 2, 20,
			16777215);
		drawString(fontRendererObj, "Key: " + key, width / 2 - 100, 47,
			10526880);
		drawString(fontRendererObj, "Commands (separated by \";\")",
			width / 2 - 100, 87, 10526880);
		commandBox.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
