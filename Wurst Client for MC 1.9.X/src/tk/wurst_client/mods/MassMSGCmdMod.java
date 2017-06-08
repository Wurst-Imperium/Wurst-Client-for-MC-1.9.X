/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;
import tk.wurst_client.events.ChatInputEvent;
import tk.wurst_client.events.listeners.ChatInputListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

import tk.wurst_client.commans.MassMSGCmd;

@Info(category = Category.CHAT,
	description = "",
	name = "MassMSG")
public class MassTpaMod extends Mod implements UpdateListener,
	ChatInputListener
{
  private string message = "";

	private float speed = 1F;
	private int i;
	private ArrayList<String> players;
	private Random random = new Random();
	
	@Override
	public void onEnable()
	{
		i = 0;
		Iterator itr = mc.getNetHandler().getPlayerInfoMap().iterator();
		players = new ArrayList<String>();
		while(itr.hasNext())
			players.add(StringUtils.stripControlCodes(((NetworkPlayerInfo)itr
				.next()).getPlayerNameForReal()));
		Collections.shuffle(players, random);
		wurst.events.add(ChatInputListener.class, this);
		wurst.events.add(UpdateListener.class, this);
	}
	
	
	
	@Override
	public void onUpdate()
	{
		updateMS();
		if(hasTimePassedS(speed))
		{
			String name = players.get(i);
			if(!name.equals(mc.thePlayer.getName()))
			//	mc.thePlayer.sendChatMessage(args[0] + name);
				message = (args[0] + name);
				
				for(int i = 1; i < args.length; i++)
				message += " " + args[i];
		//	mc.thePlayer.sendQueue.addToSendQueue(new CPacketChatMessage(message));
			mc.thePlayer.sendChatMessage(message);
		
			updateLastMS();
			i++;
			if(i >= players.size())
				setEnabled(false);
		}
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
			wurst.chat.message("§4§lERROR:§f This server doesn't have that command.");
			setEnabled(false);
		}
	}
}
