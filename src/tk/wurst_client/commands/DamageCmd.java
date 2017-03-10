/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayer.C04PacketPlayerPosition;
import tk.wurst_client.utils.MiscUtils;

@Cmd.Info(description = "Applies the given amount of damage.",
	name = "damage",
	syntax = {"<amount>"},
	help = "Commands/damage")
public class DamageCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length == 0)
			syntaxError();
		
		// check amount
		if(!MiscUtils.isInteger(args[0]))
			syntaxError("Amount must be a number.");
		int dmg = Integer.parseInt(args[0]);
		if(dmg < 1)
			error("Amount must be at least 1.");
		if(dmg > 40)
			error("Amount must be at most 20.");
		
		// check gamemode
		if(mc.thePlayer.capabilities.isCreativeMode)
			error("Cannot damage in creative mode.");
		
		double posX = mc.thePlayer.posX;
		double posY = mc.thePlayer.posY;
		double posZ = mc.thePlayer.posZ;
		NetHandlerPlayClient sendQueue = mc.thePlayer.sendQueue;
		
		// apply damage
		for(int i = 0; (double)i < 80 + 20 * (dmg - 1D); ++i)
		{
			sendQueue.addToSendQueue(new C04PacketPlayerPosition(posX,
				posY + 0.049D, posZ, false));
			sendQueue.addToSendQueue(new C04PacketPlayerPosition(posX, posY,
				posZ, false));
		}
		sendQueue.addToSendQueue(new C04PacketPlayerPosition(posX, posY, posZ,
			true));
	}
}
