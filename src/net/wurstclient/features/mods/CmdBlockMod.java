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
import net.minecraft.nbt.NBTTagString;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.gui.mods.GuiCmdBlock;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.InventoryUtils;

@Mod.Info(
	description = "Allows you to make a Command Block without having OP.\n"
		+ "Requires creative mode.\n" + "Appears to be patched on Spigot.",
	name = "CMD-Block",
	tags = "CmdBlock, CommandBlock, cmd block, command block",
	help = "Mods/CMD-Block")
@Mod.Bypasses
public final class CmdBlockMod extends Mod
{
	@Override
	public void onEnable()
	{
		// check gamemode
		if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
		{
			ChatUtils.error("Creative mode only.");
			setEnabled(false);
			return;
		}
		
		// show cmd-block screen
		mc.displayGuiScreen(new GuiCmdBlock(this, mc.currentScreen));
		setEnabled(false);
	}
	
	public void createCmdBlock(String cmd)
	{
		// generate cmd-block
		ItemStack stack = new ItemStack(Blocks.COMMAND_BLOCK);
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		nbtTagCompound.setTag("Command", new NBTTagString(cmd));
		stack.writeToNBT(nbtTagCompound);
		stack.setTagInfo("BlockEntityTag", nbtTagCompound);
		
		// give cmd-block
		if(InventoryUtils.placeStackInHotbar(stack))
			ChatUtils.message("Command Block created.");
		else
			ChatUtils.error("Please clear a slot in your hotbar.");
	}
}
