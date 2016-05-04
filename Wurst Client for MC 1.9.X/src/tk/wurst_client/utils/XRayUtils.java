/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.utils;

import java.util.Collections;
import java.util.Comparator;

import net.minecraft.block.Block;
import tk.wurst_client.mods.XRayMod;

public class XRayUtils
{
	public static void initXRayBlocks()
	{
		XRayMod.xrayBlocks.add(Block.getBlockFromName("coal_ore"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("iron_ore"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("gold_ore"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("redstone_ore"));
		XRayMod.xrayBlocks.add(Block.getBlockById(74));// Redstone ore glowing
		XRayMod.xrayBlocks.add(Block.getBlockFromName("lapis_ore"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("diamond_ore"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("emerald_ore"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("quartz_ore"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("clay"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("glowstone"));
		XRayMod.xrayBlocks.add(Block.getBlockById(8));// Water
		XRayMod.xrayBlocks.add(Block.getBlockById(9));// Water flowing
		XRayMod.xrayBlocks.add(Block.getBlockById(10));// Lava
		XRayMod.xrayBlocks.add(Block.getBlockById(11));// Lava flowing
		XRayMod.xrayBlocks.add(Block.getBlockFromName("crafting_table"));
		XRayMod.xrayBlocks.add(Block.getBlockById(61));// Furnace
		XRayMod.xrayBlocks.add(Block.getBlockById(62));// Furnace on
		XRayMod.xrayBlocks.add(Block.getBlockFromName("torch"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("ladder"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("tnt"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("coal_block"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("iron_block"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("gold_block"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("diamond_block"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("emerald_block"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("redstone_block"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("lapis_block"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("fire"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("mossy_cobblestone"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("mob_spawner"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("end_portal_frame"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("enchanting_table"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("bookshelf"));
		XRayMod.xrayBlocks.add(Block.getBlockFromName("command_block"));
	}
	
	public static boolean isXRayBlock(Block blockToCheck)
	{
		if(XRayMod.xrayBlocks.contains(blockToCheck))
			return true;
		return false;
	}
	
	public static void sortBlocks()
	{
		Collections.sort(XRayMod.xrayBlocks, new Comparator<Block>()
		{
			@Override
			public int compare(Block o1, Block o2)
			{
				return Block.getIdFromBlock(o1) - Block.getIdFromBlock(o2);
			}
		});
	}
}
