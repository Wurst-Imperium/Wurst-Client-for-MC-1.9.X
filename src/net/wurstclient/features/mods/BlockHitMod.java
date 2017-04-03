/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.LeftClickEvent;
import net.wurstclient.events.listeners.LeftClickListener;

@Mod.Info(
	description = "Automatically blocks whenever you hit something with a\n"
		+ "sword. Some say that you will receive less damage in PVP when doing\n"
		+ "this.",
	name = "BlockHit",
	tags = "AutoBlock, BlockHitting, auto block, block hitting",
	help = "Mods/BlockHit")
@Mod.Bypasses
public class BlockHitMod extends Mod implements LeftClickListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(LeftClickListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
	}
	
	@Override
	public void onLeftClick(LeftClickEvent event)
	{
		ItemStack stack = WMinecraft.getPlayer().inventory.getCurrentItem();
		
		if(stack != null && stack.getItem() instanceof ItemSword)
			doBlock();
	}
	
	public void doBlock()
	{
		if(!isActive())
			return;
		new Thread("BlockHit")
		{
			@Override
			public void run()
			{
				KeyBinding keybindUseItem = mc.gameSettings.keyBindUseItem;
				keybindUseItem.pressed = false;
				try
				{
					Thread.sleep(50);
				}catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				keybindUseItem.pressed = true;
				try
				{
					Thread.sleep(100);
				}catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				keybindUseItem.pressed = false;
			}
		}.start();
	}
}
