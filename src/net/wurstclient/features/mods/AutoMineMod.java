/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.material.Material;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.events.listeners.UpdateListener;

@Mod.Info(
	description = "Automatically mines a block as soon as you look at it.",
	name = "AutoMine",
	tags = "AutoBreak, auto mine, auto break",
	help = "Mods/AutoMine")
@Mod.Bypasses
public final class AutoMineMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		
		// release attack key
		mc.gameSettings.keyBindAttack.pressed = false;
	}
	
	@Override
	public void onUpdate()
	{
		// check hitResult
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null)
			return;
		
		// if attack key is down but nothing happens, release it for one tick
		if(mc.gameSettings.keyBindAttack.pressed
			&& !mc.playerController.getIsHittingBlock())
		{
			mc.gameSettings.keyBindAttack.pressed = false;
			return;
		}
		
		// press attack key if looking at block
		mc.gameSettings.keyBindAttack.pressed = WBlock
			.getMaterial(mc.objectMouseOver.getBlockPos()) != Material.AIR;
	}
}
