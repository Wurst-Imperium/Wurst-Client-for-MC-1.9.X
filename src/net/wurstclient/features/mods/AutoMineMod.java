/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.block.Block;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;

@Mod.Info(
	description = "Automatically mines a block as soon as you look at it.",
	name = "AutoMine",
	tags = "AutoBreak, auto mine, auto break",
	help = "Mods/AutoMine")
@Mod.Bypasses
public class AutoMineMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		mc.gameSettings.keyBindAttack.pressed = false;
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null)
			return;
		if(Block.getIdFromBlock(WMinecraft.getWorld()
			.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()) != 0)
			mc.gameSettings.keyBindAttack.pressed = true;
		else
			mc.gameSettings.keyBindAttack.pressed = false;
		
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.gameSettings.keyBindAttack.pressed = false;
	}
}
