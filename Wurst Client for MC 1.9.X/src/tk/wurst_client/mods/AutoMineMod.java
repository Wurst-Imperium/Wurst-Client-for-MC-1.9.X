/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.block.Block;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.BLOCKS,
	description = "Automatically mines a block as soon as you look at it.",
	name = "AutoMine",
	tags = "AutoBreak, auto mine, auto break",
	tutorial = "Mods/AutoMine")
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
		if(Block.getIdFromBlock(mc.theWorld.getBlockState(
			mc.objectMouseOver.getBlockPos()).getBlock()) != 0)
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
