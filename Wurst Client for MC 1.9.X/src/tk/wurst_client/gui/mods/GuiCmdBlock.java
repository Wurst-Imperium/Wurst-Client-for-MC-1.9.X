/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.mods;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

import tk.wurst_client.WurstClient;
import tk.wurst_client.mods.CmdBlockMod;

public class GuiCmdBlock extends GuiScreen
{
	private GuiScreen prevMenu;
	private CmdBlockMod mod;
	private GuiTextField commandBox;
	
	public GuiCmdBlock(CmdBlockMod mod, GuiScreen prevMenu)
	{
		this.mod = mod;
		this.prevMenu = prevMenu;
	}
	
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 3 * 2, 200,
			20, "Done"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 3 * 2 + 24,
			200, 20, "Cancel"));
		commandBox =
			new GuiTextField(0, fontRendererObj, width / 2 - 100, 60, 200, 20);
		commandBox.setMaxStringLength(100);
		commandBox.setFocused(true);
		commandBox.setText("/");
		WurstClient.INSTANCE.analytics.trackPageView("/cmdblock", "CMD-Block");
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(!button.enabled)
			return;
		switch(button.id)
		{
			case 0:
				Minecraft.getMinecraft().displayGuiScreen(prevMenu);
				mod.createCmdBlock(commandBox.getText());
				WurstClient.INSTANCE.analytics.trackEvent("cmdblock", "create");
				break;
			case 1:
				Minecraft.getMinecraft().displayGuiScreen(prevMenu);
				WurstClient.INSTANCE.analytics.trackEvent("cmdblock", "cancel");
				break;
			default:
				break;
		}
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
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		commandBox.textboxKeyTyped(par1, par2);
	}
	
	/**
	 * "Called when the screen is unloaded. Used to disable keyboard repeat events."
	 */
	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
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
		commandBox.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "CMD-Block", width / 2, 20,
			16777215);
		drawString(fontRendererObj, "Command", width / 2 - 100, 47, 10526880);
		drawCenteredString(fontRendererObj,
			"The command you type in here will be", width / 2, 100, 10526880);
		drawCenteredString(fontRendererObj, "executed by the Command Block.",
			width / 2, 110, 10526880);
		commandBox.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
