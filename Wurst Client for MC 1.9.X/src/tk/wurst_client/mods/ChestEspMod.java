/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import tk.wurst_client.events.listeners.RenderListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.utils.RenderUtils;

@Info(category = Category.RENDER,
	description = "Allows you to see chests through walls.",
	name = "ChestESP")
public class ChestEspMod extends Mod implements RenderListener
{
	private int maxChests = 1000;
	public boolean shouldInform = true;
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.itemEspMod, wurst.mods.searchMod,
			wurst.mods.xRayMod};
	}
	
	@Override
	public void onEnable()
	{
		shouldInform = true;
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onRender()
	{
		int i = 0;
		for(Object o : mc.theWorld.loadedTileEntityList)
		{
			if(i >= maxChests)
				break;
			if(o instanceof TileEntityChest)
			{
				i++;
				RenderUtils.blockESPBox(((TileEntityChest)o).getPos());
			}else if(o instanceof TileEntityEnderChest)
			{
				i++;
				RenderUtils.blockESPBox(((TileEntityEnderChest)o).getPos());
			}
		}
		for(Object o : mc.theWorld.loadedEntityList)
		{
			if(i >= maxChests)
				break;
			if(o instanceof EntityMinecartChest)
			{
				i++;
				RenderUtils.blockESPBox(((EntityMinecartChest)o).getPosition());
			}
		}
		if(i >= maxChests && shouldInform)
		{
			wurst.chat.warning(getName() + " found §lA LOT§r of chests.");
			wurst.chat.message("To prevent lag, it will only show the first "
				+ maxChests + " chests.");
			shouldInform = false;
		}else if(i < maxChests)
			shouldInform = true;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(RenderListener.class, this);
	}
}
