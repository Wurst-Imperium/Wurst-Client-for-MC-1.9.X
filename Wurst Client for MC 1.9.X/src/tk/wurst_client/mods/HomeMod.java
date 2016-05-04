/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import tk.wurst_client.events.ChatInputEvent;
import tk.wurst_client.events.listeners.ChatInputListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.CHAT,
	description = "Types \"/home\" instantly.",
	name = "/home",
	tutorial = "Mods/home")
public class HomeMod extends Mod implements UpdateListener, ChatInputListener
{
	private int disableTimer;
	
	@Override
	public void onEnable()
	{
		disableTimer = 0;
		wurst.events.add(ChatInputListener.class, this);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(disableTimer == 4)
			setEnabled(false);
		else if(disableTimer == 0)
			mc.thePlayer.sendChatMessage("/home");
		disableTimer++;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(ChatInputListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		String message = event.getComponent().getUnformattedText();
		if(message.startsWith("§c[§6Wurst§c]§f "))
			return;
		if(message.toLowerCase().contains("/help")
			|| message.toLowerCase().contains("permission"))
		{
			event.cancel();
			wurst.chat.error("This server doesn't have /home.");
		}
	}
}
