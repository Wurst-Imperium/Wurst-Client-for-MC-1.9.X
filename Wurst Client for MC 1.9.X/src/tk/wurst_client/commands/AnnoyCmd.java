/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.events.ChatInputEvent;
import tk.wurst_client.events.listeners.ChatInputListener;

@Info(help = "Annoys a player by repeating everything he says.",
	name = "annoy",
	syntax = {"[<player>]"})
public class AnnoyCmd extends Cmd implements ChatInputListener
{
	private boolean toggled;
	private String name;
	
	@Override
	public void execute(String[] args) throws Error
	{
		toggled = !toggled;
		if(toggled)
		{
			if(args.length == 1)
			{
				name = args[0];
				wurst.chat.message("Now annoying " + name + ".");
				if(name.equals(mc.thePlayer.getName()))
					wurst.chat.warning("Annoying yourself is a bad idea!");
				wurst.events.add(ChatInputListener.class, this);
			}else
			{
				toggled = false;
				syntaxError();
			}
		}else
		{
			wurst.events.remove(ChatInputListener.class, this);
			if(name != null)
			{
				wurst.chat.message("No longer annoying " + name + ".");
				name = null;
			}
		}
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		String message = new String(event.getComponent().getUnformattedText());
		if(message.startsWith("§c[§6Wurst§c]§f "))
			return;
		if(message.startsWith("<" + name + ">") || message.contains(name + ">"))
		{
			String repeatMessage = message.substring(message.indexOf(">") + 1);
			mc.thePlayer.sendChatMessage(repeatMessage);
		}else if(message.contains("] " + name + ":")
			|| message.contains("]" + name + ":"))
		{
			String repeatMessage = message.substring(message.indexOf(":") + 1);
			mc.thePlayer.sendChatMessage(repeatMessage);
		}
	}
}
