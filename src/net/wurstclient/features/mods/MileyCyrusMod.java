/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.events.listeners.UpdateListener;

@Mod.Info(description = "Makes you twerk like Miley Cyrus!",
	name = "MileyCyrus",
	tags = "miley cyrus, twerk",
	help = "Mods/MileyCyrus")
@Mod.Bypasses
public final class MileyCyrusMod extends Mod implements UpdateListener
{
	private int timer;
	
	@Override
	public void onEnable()
	{
		timer = 0;
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.gameSettings.keyBindSneak.pressed = false;
	}
	
	@Override
	public void onUpdate()
	{
		timer++;
		if(timer >= 6)
		{
			mc.gameSettings.keyBindSneak.pressed =
				!mc.gameSettings.keyBindSneak.pressed;
			timer = 0;
		}
	}
}
