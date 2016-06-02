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
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.CHAT,
	description = "Sends a private message to all players.\n"
		+ "Useful if: \nRegular chat is disabled/you are muted \nYou want to make your message seem less like a broadcast ",
	name = "MassTell",
	tags = "mass tell",
	help = "Mods/MassTell")
@Bypasses
public class MassTell extends Mod implements UpdateListener,
	ChatInputListener
{
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
				mc.thePlayer.sendChatMessage("/tell " + name);
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
}
