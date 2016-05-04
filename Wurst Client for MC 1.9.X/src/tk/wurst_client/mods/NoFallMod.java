/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.network.play.client.CPacketPlayer;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.MOVEMENT,
	description = "Protects you from fall damage.\n" + "Bypasses AntiCheat.",
	name = "NoFall",
	tags = "no fall",
	tutorial = "Mods/NoFall")
public class NoFallMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.thePlayer.fallDistance > 2)
			mc.thePlayer.sendQueue.addToSendQueue(new CPacketPlayer(true));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
