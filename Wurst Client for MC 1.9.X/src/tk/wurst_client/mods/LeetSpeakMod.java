/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

import tk.wurst_client.events.ChatOutputEvent;
import tk.wurst_client.events.listeners.ChatOutputListener;

@Info(name = "LeetSpeak",
	description = "Replaces some character with funny leet\n"
		+ "characters. e.g. Th3 qu1ck br0wn f0x...",
	category = Category.CHAT,
	tags = "l33t,1337,31337,3l33t")
public class LeetSpeakMod extends Mod implements ChatOutputListener
{
	
	
	@Override
	public void onEnable()
	{
		wurst.events.add(ChatOutputListener.class, this);
		if(wurst.mods.fancyChatMod.isEnabled())
			wurst.mods.fancyChatMod.setEnabled(false);
	}
	
@Override
	public void onSentMessage(ChatOutputEvent event)
	{
		if(event.getMessage().startsWith("/")
			|| event.getMessage().startsWith("."))
			return;
		
		String out = event.getmessage();
		
		out = out.replace("a", "4");
		out = out.replace("A", "4");
		
		out = out.replace("e", "3");
		out = out.replace("E", "3");
				
		out = out.replace("g", "6");
		out = out.replace("G", "6");
				
		out = out.replace("h", "4");
		out = out.replace("H", "4");
				
		out = out.replace("i", "1");
		out = out.replace("I", "1");
				
		out = out.replace("o", "0");
		out = out.replace("O", "0");
				
		out = out.replace("s", "5");
		out = out.replace("S", "5");
				
		out = out.replace("l", "7");
		out = out.replace("L", "7");
		
		
		out = out.replace("h", "4");
		out = out.replace("H", "4");
		
		out = out.replace("f", "ph");
		out = out.replace("F", "Ph");
		
		out = out.replace("u", "|_|");
		out = out.replace("U", "|_|");
		
		out = out.replace("v", "\/");
		out = out.replace("V", "\/");
		
		out = out.replace("n", "|\|");
		out = out.replace("N", "|\|");
		
		
		event.setMessage(out);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(ChatOutputListener.class, this);
	}
}
