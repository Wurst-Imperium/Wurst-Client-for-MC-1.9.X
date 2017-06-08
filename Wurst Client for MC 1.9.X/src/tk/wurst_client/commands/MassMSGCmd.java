/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import tk.wurst_client.commands.Cmd.Info;
import net.minecraft.network.play.client.CPacketChatMessage;

@Info(help = "Allows you to spam other players text.",
	name = "massmsg",
	syntax = {"/<command> <message>"},
public class MassTpaMod extends Cmd implements UpdateListener,
	ChatInputListener
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length < 2)
		SynatxError();
		
		if(!wurst.mods.massMsgCmdMod.isEnabled())
			wurst.mods.massMsgCmdMod.setEnabled(true);
			
		}
}
