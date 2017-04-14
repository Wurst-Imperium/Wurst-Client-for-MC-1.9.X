/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WBlock;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.utils.InventoryUtils;

@Mod.Info(
	description = "Automatically uses the best tool in your hotbar to mine blocks.\n"
		+ "Tip: This works with Nuker.",
	name = "AutoTool",
	tags = "auto tool",
	help = "Mods/AutoTool")
@Mod.Bypasses
public final class AutoToolMod extends Mod implements UpdateListener
{
	private int oldSlot = -1;
	private BlockPos pos;
	private int timer;
	
	public CheckboxSetting useSwords =
		new CheckboxSetting("Use swords as tools", false);
	
	@Override
	public void initSettings()
	{
		settings.add(useSwords);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoSwordMod, wurst.mods.nukerMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		
		// reset slot
		if(oldSlot != -1)
		{
			WMinecraft.getPlayer().inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
	}
	
	@Override
	public void onUpdate()
	{
		// set slot if mining
		if(mc.gameSettings.keyBindAttack.pressed && mc.objectMouseOver != null
			&& mc.objectMouseOver.getBlockPos() != null)
			setSlot(mc.objectMouseOver.getBlockPos());
		
		// check if slot is set
		if(oldSlot == -1)
			return;
		
		// reset slot
		if(timer <= 0)
		{
			WMinecraft.getPlayer().inventory.currentItem = oldSlot;
			oldSlot = -1;
			return;
		}
		
		// update timer
		if(!mc.gameSettings.keyBindAttack.pressed
			|| WMinecraft.getPlayer().capabilities.isCreativeMode
			|| !WBlock.canBeClicked(pos))
			timer--;
	}
	
	public void setSlot(BlockPos pos)
	{
		// check if active
		if(!isActive())
			return;
		
		// check gamemode
		if(WMinecraft.getPlayer().capabilities.isCreativeMode)
			return;
		
		// check if block can be clicked
		if(!WBlock.canBeClicked(pos))
			return;
		
		// initialize speed & slot
		float bestSpeed;
		if(WMinecraft.getPlayer().inventory.getCurrentItem() != null)
			bestSpeed = InventoryUtils.getStrVsBlock(
				WMinecraft.getPlayer().inventory.getCurrentItem(), pos);
		else
			bestSpeed = 1;
		int bestSlot = -1;
		
		// find best tool
		for(int i = 0; i < 9; i++)
		{
			// skip empty slots
			ItemStack stack =
				WMinecraft.getPlayer().inventory.getStackInSlot(i);
			if(InventoryUtils.isEmptySlot(stack))
				continue;
			
			// skip swords
			if(!useSwords.isChecked() && stack.getItem() instanceof ItemSword)
				continue;
			
			// get speed
			float speed = InventoryUtils.getStrVsBlock(stack, pos);
			
			// compare with best tool
			if(speed > bestSpeed)
			{
				bestSpeed = speed;
				bestSlot = i;
			}
		}
		
		// check if any tool was found
		if(bestSlot == -1)
			return;
		
		// save old slot
		if(oldSlot == -1)
			oldSlot = WMinecraft.getPlayer().inventory.currentItem;
		
		// set slot
		WMinecraft.getPlayer().inventory.currentItem = bestSlot;
		
		// save position
		this.pos = pos;
		
		// start timer
		timer = 4;
	}
}
