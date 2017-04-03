/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;

@Mod.Info(
	description = "Switches the item in your hand all the time.\n"
		+ "Tip: Use this in combination with BuildRandom while\n"
		+ "having a lot of different colored wool blocks in your\n" + "hotbar.",
	name = "AutoSwitch",
	tags = "auto switch",
	help = "Mods/AutoSwitch")
@Mod.Bypasses
public class AutoSwitchMod extends Mod implements UpdateListener
{
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.buildRandomMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(WMinecraft.getPlayer().inventory.currentItem == 8)
			WMinecraft.getPlayer().inventory.currentItem = 0;
		else
			WMinecraft.getPlayer().inventory.currentItem++;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
