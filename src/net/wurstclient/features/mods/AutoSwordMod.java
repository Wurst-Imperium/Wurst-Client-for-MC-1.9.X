/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickEvent;
import net.wurstclient.events.listeners.LeftClickListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;

@Mod.Info(
	description = "Automatically uses the best weapon in your hotbar to attack\n"
		+ "entities. Tip: This works with Killaura.",
	name = "AutoSword",
	tags = "auto sword",
	help = "Mods/AutoSword")
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false)
public class AutoSwordMod extends Mod
	implements LeftClickListener, UpdateListener
{
	private int oldSlot;
	private int timer;
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoToolMod};
	}
	
	@Override
	public void onEnable()
	{
		oldSlot = -1;
		wurst.events.add(LeftClickListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(timer > 0)
		{
			timer--;
			return;
		}
		WMinecraft.getPlayer().inventory.currentItem = oldSlot;
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		if(mc.objectMouseOver != null
			&& mc.objectMouseOver.entityHit instanceof EntityLivingBase)
			setSlot();
	}
	
	public static void setSlot()
	{
		if(wurst.mods.autoEatMod.isEating())
			return;
		float bestSpeed = 1F;
		int bestSlot = -1;
		for(int i = 0; i < 9; i++)
		{
			ItemStack item = WMinecraft.getPlayer().inventory.getStackInSlot(i);
			if(item == null)
				continue;
			float speed = 0;
			if(item.getItem() instanceof ItemSword)
				speed = ((ItemSword)item.getItem()).getDamageVsEntity();
			else if(item.getItem() instanceof ItemTool)
				speed = ((ItemTool)item.getItem()).getToolMaterial()
					.getDamageVsEntity();
			if(speed > bestSpeed)
			{
				bestSpeed = speed;
				bestSlot = i;
			}
		}
		if(bestSlot != -1
			&& bestSlot != WMinecraft.getPlayer().inventory.currentItem)
		{
			wurst.mods.autoSwordMod.oldSlot =
				WMinecraft.getPlayer().inventory.currentItem;
			WMinecraft.getPlayer().inventory.currentItem = bestSlot;
			wurst.mods.autoSwordMod.timer = 4;
			wurst.events.add(UpdateListener.class, wurst.mods.autoSwordMod);
		}
	}
}
