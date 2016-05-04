/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;

@Info(category = Category.BLOCKS,
	description = "Allows you to place blocks 5 times faster.\n"
		+ "Tip: This can speed up AutoBuild.",
	name = "FastPlace",
	tags = "fast place",
	tutorial = "Mods/FastPlace")
public class FastPlaceMod extends Mod implements UpdateListener
{
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.fastBreakMod,
			wurst.mods.buildRandomMod, wurst.mods.autoBuildMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		mc.rightClickDelayTimer = 0;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
