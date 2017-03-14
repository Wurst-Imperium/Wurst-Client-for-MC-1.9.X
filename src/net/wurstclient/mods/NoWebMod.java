/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.mods;

import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.mods.Mod.Bypasses;
import net.wurstclient.mods.Mod.Category;
import net.wurstclient.mods.Mod.Info;

@Info(category = Category.MOVEMENT,
	description = "Prevents you from getting slowed down in webs.\n"
		+ "Note: This has nothing to do with websites.",
	name = "NoWeb",
	tags = "no web",
	help = "Mods/NoWeb")
@Bypasses(ghostMode = false)
public class NoWebMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		mc.thePlayer.isInWeb = false;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
