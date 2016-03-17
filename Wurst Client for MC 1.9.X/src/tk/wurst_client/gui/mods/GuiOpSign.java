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
import tk.wurst_client.mods.OpSignMod;
import tk.wurst_client.utils.MiscUtils;

public class GuiOpSign extends GuiScreen
{
	private GuiScreen prevMenu;
	private OpSignMod mod;
	private GuiTextField commandBox;
	
	public GuiOpSign(OpSignMod mod, GuiScreen prevMenu)
	{
		this.mod = mod;
		this.prevMenu = prevMenu;
	}
	
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 3 * 2, 98,
			20, "Done"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 3 * 2 + 24,
			200, 20, "Cancel"));
		buttonList.add(new GuiButton(2, width / 2 + 2, height / 3 * 2, 98, 20,
			"Tutorial"));
		commandBox =
			new GuiTextField(0, fontRendererObj, width / 2 - 100, 60, 200, 20);
		commandBox.setMaxStringLength(100);
		commandBox.setFocused(true);
		commandBox.setText("/op "
			+ Minecraft.getMinecraft().session.getUsername());
		commandBox.setCursorPosition(0);
		commandBox.setSelectionPos(commandBox.getText().length());
		WurstClient.INSTANCE.analytics.trackPageView("/opsign", "OP Sign");
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
				mod.setCommand(commandBox.getText());
				WurstClient.INSTANCE.analytics.trackEvent("opsign",
					"set command");
				break;
			case 1:
				Minecraft.getMinecraft().displayGuiScreen(prevMenu);
				mod.setEnabled(false);
				WurstClient.INSTANCE.analytics.trackEvent("opsign", "cancel");
				break;
			case 2:
				MiscUtils
					.openLink("https://www.wurst-client.tk/wiki/Mods/OP-Sign_(Force_OP)");
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
		drawCenteredString(fontRendererObj, "OP Sign", width / 2, 20, 16777215);
		drawString(fontRendererObj, "Command", width / 2 - 100, 47, 10526880);
		drawCenteredString(fontRendererObj,
			"This command will be executed once", width / 2, 90, 10526880);
		drawCenteredString(fontRendererObj,
			"you place & right click on a sign.", width / 2, 100, 10526880);
		drawCenteredString(fontRendererObj,
			"§cOnly works on servers running§r", width / 2, 110, 10526880);
		drawCenteredString(fontRendererObj,
			"§c§lMinecraft 1.8 - 1.8.5 without Spigot!§r", width / 2, 120,
			10526880);
		drawCenteredString(fontRendererObj,
			"Even if these criteria are met, it is always", width / 2, 130,
			10526880);
		drawCenteredString(fontRendererObj,
			"possible to run into a server that wont work.", width / 2, 140,
			10526880);
		commandBox.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
