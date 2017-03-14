/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.mods;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.mods.Mod.Bypasses;
import net.wurstclient.mods.Mod.Category;
import net.wurstclient.mods.Mod.Info;

@Info(category = Category.MOVEMENT,
	description = "Protects you from fall damage.\n" + "Bypasses AntiCheat.",
	name = "NoFall",
	tags = "no fall",
	help = "Mods/NoFall")
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
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
