/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import tk.wurst_client.events.listeners.LeftClickListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;

@Info(category = Category.BLOCKS,
	description = "Automatically uses the best tool in your hotbar to\n"
		+ "mine blocks. Tip: This works with Nuker.",
	name = "AutoTool",
	tags = "auto tool",
	tutorial = "Mods/AutoTool")
public class AutoToolMod extends Mod implements LeftClickListener,
	UpdateListener
{
	private boolean isActive = false;
	private int oldSlot;
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.autoSwordMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(LeftClickListener.class, this);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(!mc.gameSettings.keyBindAttack.pressed && isActive)
		{
			isActive = false;
			mc.thePlayer.inventory.currentItem = oldSlot;
		}else if(isActive
			&& mc.objectMouseOver != null
			&& mc.objectMouseOver.getBlockPos() != null
			&& mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos())
				.getBlock().getMaterial(null) != Material.air)
			setSlot(mc.objectMouseOver.getBlockPos());
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
		wurst.events.remove(UpdateListener.class, this);
		isActive = false;
		mc.thePlayer.inventory.currentItem = oldSlot;
	}
	
	@Override
	public void onLeftClick()
	{
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.getBlockPos() == null)
			return;
		if(mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos())
			.getBlock().getMaterial(null) != Material.air)
		{
			isActive = true;
			oldSlot = mc.thePlayer.inventory.currentItem;
			setSlot(mc.objectMouseOver.getBlockPos());
		}
	}
	
	public static void setSlot(BlockPos blockPos)
	{
		float bestSpeed = 1F;
		int bestSlot = -1;
		IBlockState blockState = mc.theWorld.getBlockState(blockPos);
		for(int i = 0; i < 9; i++)
		{
			ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
			if(item == null)
				continue;
			float speed = item.getStrVsBlock(blockState);
			if(speed > bestSpeed)
			{
				bestSpeed = speed;
				bestSlot = i;
			}
		}
		if(bestSlot != -1)
			mc.thePlayer.inventory.currentItem = bestSlot;
	}
}
