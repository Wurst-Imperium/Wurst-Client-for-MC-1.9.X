/*
 * Copyright � 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import org.darkstorm.minecraft.gui.component.BoundedRangeComponent.ValueDisplay;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.settings.ModeSetting;
import tk.wurst_client.navigator.settings.SliderSetting;

@Info(category = Category.MISC,
	description = "Automatically eats food when necessary.",
	name = "AutoEat",
	tags = "AutoSoup,auto eat,auto soup",
	tutorial = "Mods/AutoEat")
public class AutoEatMod extends Mod implements UpdateListener
{
	private int oldSlot;
	private int bestSlot;
	private int hungerMinimal = 15;
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.autoSoupMod};
	}
	
	public void initSettings()
	{
		settings.add(new SliderSetting("Minimal", hungerMinimal, 0, 20, 1,
			ValueDisplay.INTEGER)
		{
			@Override
			public void update()
			{
				hungerMinimal = (int)getValue();				
			}
		});		
	}
	
	@Override
	public void onEnable()
	{
		oldSlot = -1;
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(oldSlot != -1 || mc.thePlayer.capabilities.isCreativeMode
			|| mc.thePlayer.getFoodStats().getFoodLevel() > hungerMinimal)
			return;
		float bestSaturation = 0F;
		bestSlot = -1;
		for(int i = 0; i < 9; i++)
		{
			ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
			if(item == null)
				continue;
			float saturation = 0;
			if(item.getItem() instanceof ItemFood)
				saturation =
					((ItemFood)item.getItem()).getSaturationModifier(item);
			if(saturation > bestSaturation)
			{
				bestSaturation = saturation;
				bestSlot = i;
			}
		}
		if(bestSlot == -1)
			return;
		oldSlot = mc.thePlayer.inventory.currentItem;
		wurst.events.add(UpdateListener.class, new UpdateListener()
		{
			@Override
			public void onUpdate()
			{
				if(!AutoEatMod.this.isActive()
					|| mc.thePlayer.capabilities.isCreativeMode
					|| mc.thePlayer.getFoodStats().getFoodLevel() >= 20)
				{
					stop();
					return;
				}
				ItemStack item =
					mc.thePlayer.inventory.getStackInSlot(bestSlot);
				if(item == null || !(item.getItem() instanceof ItemFood))
				{
					stop();
					return;
				}
				mc.thePlayer.inventory.currentItem = bestSlot;
				mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld,
					item, EnumHand.MAIN_HAND);
				mc.gameSettings.keyBindUseItem.pressed = true;
			}
			
			private void stop()
			{
				mc.gameSettings.keyBindUseItem.pressed = false;
				mc.thePlayer.inventory.currentItem = oldSlot;
				oldSlot = -1;
				wurst.events.remove(UpdateListener.class, this);
			}
		});
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	public boolean isEating()
	{
		return oldSlot != -1;
	}
}
