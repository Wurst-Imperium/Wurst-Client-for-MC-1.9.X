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

@Info(category = Category.COMBAT,
	description = "Regenerates your health 1000 times faster.\n"
		+ "Can cause unwanted \"Flying is not enabled!\" kicks.",
	name = "Regen",
	noCheatCompatible = false,
	tags = "GodMode, god mode",
	tutorial = "Mods/Regen")
public class RegenMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(!mc.thePlayer.capabilities.isCreativeMode
			&& mc.thePlayer.getFoodStats().getFoodLevel() > 17
			&& mc.thePlayer.getHealth() < 20 && mc.thePlayer.getHealth() != 0
			&& mc.thePlayer.onGround)
			for(int i = 0; i < 1000; i++)
				mc.thePlayer.sendQueue.addToSendQueue(new CPacketPlayer());
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
