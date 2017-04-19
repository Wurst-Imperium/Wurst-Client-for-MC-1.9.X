/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.compatibility.WSoundEvents;
import net.wurstclient.events.PacketInputEvent;
import net.wurstclient.events.listeners.PacketInputListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.InventoryUtils;

@Mod.Info(
	description = "Automatically catches fish until either all of your fishing rods are completely used up or your\n"
		+ "inventory is completely full. If fishing rods are placed outside of the hotbar, they will\n"
		+ "automatically be moved into the hotbar once needed.",
	name = "AutoFish",
	tags = "FishBot, auto fish, fish bot, fishing",
	help = "Mods/AutoFish")
@Mod.Bypasses
public final class AutoFishMod extends Mod
	implements UpdateListener, PacketInputListener
{
	private int timer;
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PacketInputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PacketInputListener.class, this);
		
		// reset timer
		timer = 0;
	}
	
	@Override
	public void onUpdate()
	{
		// check if inventory is full
		if(WMinecraft.getPlayer().inventory.getFirstEmptyStack() == -1)
		{
			ChatUtils.message("Inventory is full.");
			setEnabled(false);
			return;
		}
		
		// search fishing rod in hotbar
		int rodInHotbar = -1;
		for(int i = 0; i < 9; i++)
		{
			// skip non-rod items
			ItemStack stack =
				WMinecraft.getPlayer().inventory.getStackInSlot(i);
			if(InventoryUtils.isEmptySlot(stack)
				|| !(stack.getItem() instanceof ItemFishingRod))
				continue;
			
			rodInHotbar = i;
			break;
		}
		
		// check if any rod was found
		if(rodInHotbar != -1)
		{
			// select fishing rod
			if(WMinecraft.getPlayer().inventory.currentItem != rodInHotbar)
			{
				WMinecraft.getPlayer().inventory.currentItem = rodInHotbar;
				return;
			}
			
			// wait for timer
			if(timer > 0)
			{
				timer--;
				return;
			}
			
			// check bobber
			if(WMinecraft.getPlayer().fishEntity != null)
				return;
			
			// cast rod
			rightClick();
			return;
		}
		
		// search fishing rod in inventory
		int rodInInventory = -1;
		for(int i = 9; i < 36; i++)
		{
			// skip non-rod items
			ItemStack stack =
				WMinecraft.getPlayer().inventory.getStackInSlot(i);
			if(InventoryUtils.isEmptySlot(stack)
				|| !(stack.getItem() instanceof ItemFishingRod))
				continue;
			
			rodInInventory = i;
			break;
		}
		
		// check if completely out of rods
		if(rodInInventory == -1)
		{
			ChatUtils.message("Out of fishing rods.");
			setEnabled(false);
			return;
		}
		
		// find empty hotbar slot
		int hotbarSlot = -1;
		for(int i = 0; i < 9; i++)
		{
			// skip non-empty slots
			if(!InventoryUtils.isSlotEmpty(i))
				continue;
			
			hotbarSlot = i;
			break;
		}
		
		// check if hotbar is full
		boolean swap = false;
		if(hotbarSlot == -1)
		{
			hotbarSlot = WMinecraft.getPlayer().inventory.currentItem;
			swap = true;
		}
		
		// place rod in hotbar slot
		WPlayerController.windowClick_PICKUP(rodInInventory);
		WPlayerController.windowClick_PICKUP(36 + hotbarSlot);
		
		// swap old hotbar item with rod
		if(swap)
			WPlayerController.windowClick_PICKUP(rodInInventory);
	}
	
	@Override
	public void onReceivedPacket(PacketInputEvent event)
	{
		// check packet type
		if(!(event.getPacket() instanceof SPacketSoundEffect))
			return;
		
		// check sound type
		if(!WSoundEvents.isBobberSplash((SPacketSoundEffect)event.getPacket()))
			return;
		
		// catch fish
		rightClick();
	}
	
	private void rightClick()
	{
		// check held item
		ItemStack stack = WMinecraft.getPlayer().inventory.getCurrentItem();
		if(InventoryUtils.isEmptySlot(stack)
			|| !(stack.getItem() instanceof ItemFishingRod))
			return;
		
		// right click
		mc.rightClickMouse();
		
		// reset timer
		timer = 15;
	}
}
