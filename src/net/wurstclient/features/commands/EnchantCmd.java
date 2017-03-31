/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.wurstclient.events.ChatOutputEvent;
import net.wurstclient.utils.ChatUtils;

@Cmd.Info(description = "Enchants items with everything.",
	name = "enchant",
	syntax = {"[all]"},
	help = "Commands/enchant")
public final class EnchantCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(!mc.thePlayer.capabilities.isCreativeMode)
			error("Creative mode only.");
		if(args.length == 0)
		{
			ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
			if(currentItem == null)
				error("There is no item in your hand.");
			for(Enchantment enchantment : Enchantment.enchantmentRegistry)
				try
				{
					if(enchantment == Enchantments.silkTouch)
						continue;
					currentItem.addEnchantment(enchantment, 127);
				}catch(Exception e)
				{
					
				}
		}else if(args[0].equals("all"))
		{
			int items = 0;
			for(int i = 0; i < 40; i++)
			{
				ItemStack currentItem =
					mc.thePlayer.inventory.getStackInSlot(i);
				if(currentItem == null)
					continue;
				items++;
				for(Enchantment enchantment : Enchantment.enchantmentRegistry)
					try
					{
						if(enchantment == Enchantments.silkTouch)
							continue;
						currentItem.addEnchantment(enchantment, 127);
					}catch(Exception e)
					{
						
					}
			}
			if(items == 1)
				ChatUtils.message("Enchanted 1 item.");
			else
				ChatUtils.message("Enchanted " + items + " items.");
		}else
			syntaxError();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return "Enchant Current Item";
	}
	
	@Override
	public void doPrimaryAction()
	{
		wurst.commands.onSentMessage(new ChatOutputEvent(".enchant", true));
	}
}
