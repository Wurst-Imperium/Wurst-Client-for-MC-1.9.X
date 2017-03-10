/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.gui.alts;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;

import org.lwjgl.input.Keyboard;

import tk.wurst_client.WurstClient;
import tk.wurst_client.utils.JsonUtils;
import tk.wurst_client.utils.MiscUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class SessionStealerScreen extends GuiScreen
{
	protected GuiScreen prevMenu;
	protected GuiTextField tokenBox;
	
	protected String errorText = "";
	protected String helpText = "";
	
	public SessionStealerScreen(GuiScreen par1GuiScreen)
	{
		prevMenu = par1GuiScreen;
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		tokenBox.updateCursorCounter();
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 72 + 12,
			"Steal Session"));
		buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 96 + 12,
			"How to Use"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12,
			"Cancel"));
		tokenBox =
			new GuiTextField(1, fontRendererObj, width / 2 - 100, 60, 200, 20);
		tokenBox.setMaxStringLength(65);
		tokenBox.setFocused(true);
		
		WurstClient.INSTANCE.analytics.trackPageView("/session-stealer",
			"Session Stealer");
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
	protected void actionPerformed(GuiButton button)
	{
		if(button.enabled)
			if(button.id == 1)
				mc.displayGuiScreen(prevMenu);
			else if(button.id == 0)
			{
				// validate input
				String input = tokenBox.getText();
				if(input.length() != 65 || !input.substring(32, 33).equals(":")
					|| input.split(":").length != 2)
				{
					errorText = "That is not a session token!";
					helpText =
						"If you're lost, click the \"How to Use\" button.";
					WurstClient.INSTANCE.analytics.trackEvent(
						"session stealer", "invalid input");
					return;
				}
				String uuid = input.split(":")[1];
				if(uuid.contains("-"))
				{
					errorText = "That is not a session token!";
					helpText = "Try without the dashes (-).";
					WurstClient.INSTANCE.analytics.trackEvent(
						"session stealer", "dashes in token");
					return;
				}
				
				// fetch name history
				JsonElement rawJson;
				try
				{
					rawJson =
						JsonUtils.jsonParser.parse(new InputStreamReader(
							new URL("https://api.mojang.com/user/profiles/"
								+ uuid + "/names").openConnection()
								.getInputStream()));
				}catch(JsonIOException | JsonSyntaxException | IOException e)
				{
					e.printStackTrace();
					errorText = "An error occurred";
					helpText = "Mojang servers might be down.";
					WurstClient.INSTANCE.analytics.trackEvent(
						"session stealer", "mojang servers down");
					return;
				}
				
				// validate UUID
				if(!rawJson.isJsonArray())
				{
					errorText = "Invalid UUID";
					helpText = "This session is fake. Try a different one.";
					WurstClient.INSTANCE.analytics.trackEvent(
						"session stealer", "invalid uuid");
					return;
				}
				
				// get latest name
				JsonArray json = rawJson.getAsJsonArray();
				String name =
					json.get(json.size() - 1).getAsJsonObject().get("name")
						.getAsString();
				
				// validate session
				try
				{
					HttpURLConnection connection =
						(HttpURLConnection)new URL(
							"https://authserver.mojang.com/validate")
							.openConnection(Proxy.NO_PROXY);
					
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type",
						"application/json");
					
					String content =
						"{\"accessToken\":\"" + input.split(":")[0] + "\"}";
					
					connection.setRequestProperty("Content-Length", ""
						+ content.getBytes().length);
					connection.setRequestProperty("Content-Language", "en-US");
					connection.setUseCaches(false);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					
					DataOutputStream output =
						new DataOutputStream(connection.getOutputStream());
					output.writeBytes(content);
					output.flush();
					output.close();
					
					if(connection.getResponseCode() != 204)
						throw new IOException();
				}catch(IOException e)
				{
					errorText = "Invalid Session";
					helpText =
						"This token doesn't work anymore. Try a different one.";
					WurstClient.INSTANCE.analytics.trackEvent(
						"session stealer", "invalid session");
					return;
				}
				
				// use session
				mc.session =
					new Session(name, uuid, input.split(":")[0], "mojang");
				WurstClient.INSTANCE.analytics.trackEvent("session stealer",
					"success");
				mc.displayGuiScreen(prevMenu);
			}else if(button.id == 2)
			{
				MiscUtils
					.openLink("https://www.wurst-client.tk/wiki/Special_Features/Force_OP_(Session_Stealer)/");
				WurstClient.INSTANCE.analytics.trackEvent("session stealer",
					"view tutorial");
			}
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char par1, int par2)
	{
		tokenBox.textboxKeyTyped(par1, par2);
		
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
		tokenBox.mouseClicked(par1, par2, par3);
		if(tokenBox.isFocused())
		{
			errorText = "";
			helpText = "";
		}
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		
		drawCenteredString(fontRendererObj, "Session Stealer", width / 2, 20,
			16777215);
		drawString(fontRendererObj, "Session ID is token:", width / 2 - 100,
			47, 10526880);
		drawCenteredString(fontRendererObj, "§c" + errorText, width / 2, 96,
			0xFFFFFF);
		drawCenteredString(fontRendererObj, "§7" + helpText, width / 2, 112,
			0xFFFFFF);
		
		tokenBox.drawTextBox();
		
		super.drawScreen(par1, par2, par3);
	}
}
