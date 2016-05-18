/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayer;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.MISC,
	description = "Allows you to eat food much faster.\n" + "OM! NOM! NOM!",
	name = "FastEat",
	noCheatCompatible = false,
	tags = "FastNom, fast eat, fast nom",
	help = "Mods/FastEat")
public class FastEatMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.thePlayer.getHealth() > 0
			&& mc.thePlayer.onGround
			&& mc.thePlayer.inventory.getCurrentItem() != null
			&& mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFood
			&& mc.thePlayer.getFoodStats().needFood()
			&& mc.gameSettings.keyBindUseItem.pressed)
			for(int i = 0; i < 100; i++)
				mc.thePlayer.sendQueue
					.addToSendQueue(new CPacketPlayer(false));
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
