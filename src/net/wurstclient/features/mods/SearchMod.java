/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.RenderUtils;

@Mod.Info(description = "Helps you to find specific blocks.\n"
	+ "Use .search id <block id> or .search name <block name>\n"
	+ "to specify it.", name = "Search", help = "Mods/Search")
@Mod.Bypasses
public final class SearchMod extends Mod
	implements UpdateListener, RenderListener
{
	private ArrayList<BlockPos> matchingBlocks = new ArrayList<>();
	private int range = 50;
	private int maxBlocks = 1000;
	public boolean notify = true;
	
	@Override
	public String getRenderName()
	{
		return getName() + " [" + wurst.options.searchID + "]";
	}
	
	@Override
	public void onEnable()
	{
		notify = true;
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		for(BlockPos blockPos : matchingBlocks)
			RenderUtils.searchBox(blockPos);
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		if(hasTimePassedM(3000))
		{
			matchingBlocks.clear();
			for(int y = range; y >= -range; y--)
			{
				for(int x = range; x >= -range; x--)
				{
					for(int z = range; z >= -range; z--)
					{
						int posX = (int)(WMinecraft.getPlayer().posX + x);
						int posY = (int)(WMinecraft.getPlayer().posY + y);
						int posZ = (int)(WMinecraft.getPlayer().posZ + z);
						BlockPos pos = new BlockPos(posX, posY, posZ);
						if(Block.getIdFromBlock(
							WMinecraft.getWorld().getBlockState(pos)
								.getBlock()) == wurst.options.searchID)
							matchingBlocks.add(pos);
						if(matchingBlocks.size() >= maxBlocks)
							break;
					}
					if(matchingBlocks.size() >= maxBlocks)
						break;
				}
				if(matchingBlocks.size() >= maxBlocks)
					break;
			}
			if(matchingBlocks.size() >= maxBlocks && notify)
			{
				ChatUtils.warning(getName() + " found §lA LOT§r of blocks.");
				ChatUtils.message("To prevent lag, it will only show the first "
					+ maxBlocks + " blocks.");
				notify = false;
			}else if(matchingBlocks.size() < maxBlocks)
				notify = true;
			updateLastMS();
		}
	}
}
