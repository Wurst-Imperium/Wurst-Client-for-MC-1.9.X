/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.gui.multiplayer;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.wurstclient.WurstClient;
import net.wurstclient.utils.MiscUtils;

public class GuiUseProxy extends GuiScreen
{
	private GuiMultiplayer prevScreen;
	private GuiTextField proxyBox;
	private String error = "";
	
	public GuiUseProxy(GuiMultiplayer prevMultiplayerMenu)
	{
		prevScreen = prevMultiplayerMenu;
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		proxyBox.updateCursorCounter();
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(
			new GuiButton(0, width / 2 - 100, height / 4 + 120 + 12, "Cancel"));
		buttonList.add(
			new GuiButton(1, width / 2 - 100, height / 4 + 72 + 12, "Connect"));
		buttonList.add(
			new GuiButton(2, width / 2 - 100, height / 4 + 96 + 12, "Reset"));
		proxyBox =
			new GuiTextField(0, fontRendererObj, width / 2 - 100, 60, 200, 20);
		proxyBox.setFocused(true);
		WurstClient.INSTANCE.analytics.trackPageView("/multiplayer/use-proxy",
			"Use Proxy");
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
			{// Cancel
				WurstClient.INSTANCE.analytics.trackEvent("use proxy",
					"cancel");
				mc.displayGuiScreen(prevScreen);
			}else if(clickedButton.id == 1)
			{// Connect
				WurstClient.INSTANCE.analytics.trackEvent("use proxy",
					"connect");
				
				// must contain ':' once
				if(!proxyBox.getText().contains(":")
					|| proxyBox.getText().split(":").length != 2)
				{
					WurstClient.INSTANCE.analytics.trackEvent("use proxy",
						"error", "not a proxy");
					error = "Not a proxy!";
					return;
				}
				
				String[] parts = proxyBox.getText().split(":");
				
				// validate port
				if(!MiscUtils.isInteger(parts[1])
					|| Integer.parseInt(parts[1]) > 65536
					|| Integer.parseInt(parts[1]) < 0)
				{
					WurstClient.INSTANCE.analytics.trackEvent("use proxy",
						"error", "invalid port");
					error = "Invalid port!";
					return;
				}
				
				try
				{
					System.setProperty("socksProxyHost", parts[0]);
					System.setProperty("socksProxyPort", parts[1]);
				}catch(Exception e)
				{
					WurstClient.INSTANCE.analytics.trackEvent("use proxy",
						"exception", e.toString());
					error = e.toString();
					return;
				}
				
				if(error.isEmpty())
				{
					WurstClient.INSTANCE.analytics.trackEvent("use proxy",
						"success");
					mc.displayGuiScreen(prevScreen);
				}else
					WurstClient.INSTANCE.analytics.trackEvent("use proxy",
						"error", error);
			}else if(clickedButton.id == 2)
			{// Reset
				WurstClient.INSTANCE.analytics.trackEvent("use proxy", "reset");
				
				System.setProperty("socksProxyHost", "");
				System.setProperty("socksProxyPort", "");
				
				mc.displayGuiScreen(prevScreen);
			}
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		proxyBox.textboxKeyTyped(par1, par2);
		
		if(par2 == 28 || par2 == 156)
			actionPerformed(buttonList.get(1));
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
		proxyBox.mouseClicked(par1, par2, par3);
		if(proxyBox.isFocused())
			error = "";
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "Use Proxy", width / 2, 20,
			0xFFFFFF);
		drawString(fontRendererObj, "IP:Port (must be a SOCKS proxy)",
			width / 2 - 100, 47, 0xA0A0A0);
		drawCenteredString(fontRendererObj, error, width / 2, 87, 0xFF0000);
		String currentProxy = System.getProperty("socksProxyHost") + ":"
			+ System.getProperty("socksProxyPort");
		if(currentProxy.equals(":") || currentProxy.equals("null:null"))
			currentProxy = "none";
		drawString(fontRendererObj, "Current proxy: " + currentProxy,
			width / 2 - 100, 97, 0xA0A0A0);
		proxyBox.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
