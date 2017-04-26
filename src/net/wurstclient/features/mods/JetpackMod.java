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
import net.wurstclient.features.Mod;
import net.wurstclient.features.SearchTags;

@SearchTags({"jet pack"})
@Mod.Info(help = "Mods/Jetpack")
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false,
	mineplex = false)
public final class JetpackMod extends Mod implements UpdateListener
{
	public JetpackMod()
	{
		super("Jetpack", "Allows you to jump in mid-air.\n"
			+ "Looks as if you had a jetpack.");
	}
	
	@Override
	public void onEnable()
	{
		if(wurst.mods.flightMod.isEnabled())
			wurst.mods.flightMod.setEnabled(false);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.gameSettings.keyBindJump.pressed)
			WMinecraft.getPlayer().jump();
	}
}
