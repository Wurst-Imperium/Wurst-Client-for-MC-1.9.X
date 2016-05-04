/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.util.ArrayList;

import net.minecraft.block.Block;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.RENDER,
	description = "Allows you to see ores through walls.",
	name = "X-Ray",
	tags = "xray, x ray",
	tutorial = "Mods/X-Ray")
public class XRayMod extends Mod
{
	public static ArrayList<Block> xrayBlocks = new ArrayList<Block>();
	
	@Override
	public String getRenderName()
	{
		return "X-Wurst";
	}
	
	@Override
	public void onToggle()
	{
		mc.renderGlobal.loadRenderers();
	}
}
