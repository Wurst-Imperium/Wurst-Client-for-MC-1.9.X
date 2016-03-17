/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.MISC,
	description = "Blocks bad potion effects.",
	name = "AntiPotion",
	noCheatCompatible = false)
public class AntiPotionMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		EntityPlayerSP player = mc.thePlayer;
		if(!player.capabilities.isCreativeMode && player.onGround
			&& !player.getActivePotionEffects().isEmpty())
			if(player.isPotionActive(MobEffects.hunger)
				|| player.isPotionActive(MobEffects.moveSlowdown)
				|| player.isPotionActive(MobEffects.digSlowdown)
				|| player.isPotionActive(MobEffects.harm)
				|| player.isPotionActive(MobEffects.confusion)
				|| player.isPotionActive(MobEffects.blindness)
				|| player.isPotionActive(MobEffects.weakness)
				|| player.isPotionActive(MobEffects.wither)
				|| player.isPotionActive(MobEffects.poison))
				for(int i = 0; i < 1000; i++)
					player.sendQueue.addToSendQueue(new CPacketPlayer());
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
