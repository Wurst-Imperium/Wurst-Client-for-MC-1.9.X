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
import net.wurstclient.utils.XRayUtils;

@Mod.Info(description = "Allows you to see ores through walls.",
	name = "X-Ray",
	tags = "xray, x ray",
	help = "Mods/X-Ray")
@Mod.Bypasses
public final class XRayMod extends Mod
{
	public static ArrayList<Block> xrayBlocks;
	
	static
	{
		xrayBlocks = new ArrayList<>();
		XRayUtils.initXRayBlocks();
	}
	
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
