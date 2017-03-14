/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.wurstclient.features.commands.Cmd.Info;

@Info(description = "Sends a chat message, even if the message starts with a dot.",
	name = "say",
	syntax = {"<message>"},
	tags = ".legit, dots in chat, command bypass, prefix",
	help = "Commands/say")
public class SayCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
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
