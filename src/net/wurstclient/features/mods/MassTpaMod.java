/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.ChatInputEvent;
import net.wurstclient.events.listeners.ChatInputListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.utils.ChatUtils;

@Mod.Info(
	description = "Sends a TPA request to all players.\n"
		+ "Stops if someone accepts.",
	name = "MassTPA",
	tags = "mass tpa",
	help = "Mods/MassTPA")
@Mod.Bypasses
public final class MassTpaMod extends Mod implements UpdateListener, ChatInputListener
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
		players = new ArrayList<>();
		while(itr.hasNext())
			players.add(StringUtils.stripControlCodes(
				((NetworkPlayerInfo)itr.next()).getPlayerNameForReal()));
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
			if(!name.equals(WMinecraft.getPlayer().getName()))
				WMinecraft.getPlayer().sendChatMessage("/tpa " + name);
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
			ChatUtils.message("§4§lERROR:§f This server doesn't have TPA.");
			setEnabled(false);
		}else if(message.toLowerCase().contains("accepted")
			&& message.toLowerCase().contains("request")
			|| message.toLowerCase().contains("akzeptiert")
				&& message.toLowerCase().contains("anfrage"))
		{
			event.cancel();
			ChatUtils.message("Someone accepted your TPA request. Stopping.");
			setEnabled(false);
		}
	}
}
