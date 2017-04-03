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

@Mod.Info(description = "Allows you to climb up ladders twice as fast.",
	name = "FastLadder",
	tags = "FastClimb, fast ladder, fast climb",
	help = "Mods/FastLadder")
@Mod.Bypasses(ghostMode = false)
public final class FastLadderMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(WMinecraft.getPlayer().isOnLadder()
			&& WMinecraft.getPlayer().isCollidedHorizontally)
			WMinecraft.getPlayer().motionY = 0.2872;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
