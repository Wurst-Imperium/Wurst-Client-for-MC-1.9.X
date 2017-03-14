/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.material.Material;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;

@Info(category = Category.MOVEMENT,
	description = "Makes you fall like if you had a hang glider.",
	name = "Glide",
	help = "Mods/Glide")
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public class GlideMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.thePlayer.motionY < 0 && mc.thePlayer.isAirBorne
			&& !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder()
			&& !mc.thePlayer.isInsideOfMaterial(Material.lava))
		{
			mc.thePlayer.motionY = -0.125f;
			mc.thePlayer.jumpMovementFactor *= 1.21337f;
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
