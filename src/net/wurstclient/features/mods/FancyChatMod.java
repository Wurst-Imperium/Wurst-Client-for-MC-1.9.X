/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.events.ChatOutputEvent;
import net.wurstclient.events.listeners.ChatOutputListener;

@Mod.Info(
	description = "Replaces ASCII characters in sent chat messages with fancier unicode characters. Can be\n"
		+ "used to bypass curse word filters on some servers. Does not work on servers that block\n"
		+ "unicode characters.",
	name = "FancyChat",
	tags = "fancy chat",
	help = "Mods/FancyChat")
@Mod.Bypasses(ghostMode = false, mineplex = false)
public final class FancyChatMod extends Mod implements ChatOutputListener
{
	private final String blacklist = "(){}[]|";
	
	@Override
	public void onEnable()
	{
		wurst.events.add(ChatOutputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(ChatOutputListener.class, this);
	}
	
	@Override
	public void onSentMessage(ChatOutputEvent event)
	{
		if(event.getMessage().startsWith("/")
			|| event.getMessage().startsWith("."))
			return;
		
		String out = "";
		
		for(char chr : event.getMessage().toCharArray())
			if(chr >= 0x21 && chr <= 0x80
				&& !blacklist.contains(Character.toString(chr)))
				out += new String(Character.toChars(chr + 0xFEE0));
			else
				out += chr;
			
		event.setMessage(out);
	}
}
