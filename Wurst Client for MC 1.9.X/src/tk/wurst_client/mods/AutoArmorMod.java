/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.COMBAT,
	description = "Manages your armor automatically.",
	name = "AutoArmor")
public class AutoArmorMod extends Mod implements UpdateListener
{
	private int[] bestArmor;
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.thePlayer.capabilities.isCreativeMode
			|| mc.currentScreen instanceof GuiContainer
			&& !(mc.currentScreen instanceof GuiInventory))
			return;
		updateMS();
		if(hasTimePassedM(3000))
		{
			bestArmor = new int[4];
			for(int i = 0; i < bestArmor.length; i++)
				bestArmor[i] = -1;
			for(int i = 0; i < 36; i++)
			{
				ItemStack itemstack = mc.thePlayer.inventory.getStackInSlot(i);
				if(itemstack != null
					&& itemstack.getItem() instanceof ItemArmor)
				{
					ItemArmor armor = (ItemArmor)itemstack.getItem();
					if(armor.damageReduceAmount > bestArmor[3 - armor.armorType])
						bestArmor[3 - armor.armorType] = i;
				}
			}
			for(int i = 0; i < 4; i++)
			{
				ItemStack itemstack = mc.thePlayer.inventory.armorItemInSlot(i);
				ItemArmor currentArmor;
				if(itemstack != null
					&& itemstack.getItem() instanceof ItemArmor)
					currentArmor = (ItemArmor)itemstack.getItem();
				else
					currentArmor = null;
				ItemArmor bestArmor;
				try
				{
					bestArmor =
						(ItemArmor)mc.thePlayer.inventory.getStackInSlot(
							this.bestArmor[i]).getItem();
				}catch(Exception e)
				{
					bestArmor = null;
				}
				if(bestArmor != null
					&& (currentArmor == null || bestArmor.damageReduceAmount > currentArmor.damageReduceAmount))
					if(mc.thePlayer.inventory.getFirstEmptyStack() != -1
						|| currentArmor == null)
					{
						mc.playerController.windowClick(0, 8 - i, 0,
							ClickType.PICKUP, mc.thePlayer);
						mc.playerController.windowClick(0,
							this.bestArmor[i] < 9 ? 36 + this.bestArmor[i]
								: this.bestArmor[i], 0, ClickType.PICKUP,
							Minecraft.getMinecraft().thePlayer);
					}
			}
			updateLastMS();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
