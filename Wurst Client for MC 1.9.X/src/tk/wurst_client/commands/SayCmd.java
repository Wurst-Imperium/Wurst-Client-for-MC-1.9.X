/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import net.minecraft.network.play.client.CPacketChatMessage;
import tk.wurst_client.commands.Cmd.Info;

@Info(help = "Sends a chat message, even if the message starts with a dot.",
	name = "say",
	syntax = {"<message>"},
	tags = ".legit, dots in chat, command bypass, prefix",
	tutorial = "Commands/say")
public class SayCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length > 0)
		{
			String message = args[0];
			for(int i = 1; i < args.length; i++)
				message += " " + args[i];
			mc.thePlayer.sendQueue.addToSendQueue(new CPacketChatMessage(
				message));
		}else
			syntaxError("Message required.");
	}
}
