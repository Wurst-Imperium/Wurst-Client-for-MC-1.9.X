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

@Info(category = Category.MOVEMENT,
	description = "Allows you to jump in mid-air.\n"
		+ "Looks as if you had a jetpack.",
	name = "Jetpack",
	noCheatCompatible = false)
public class JetpackMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		if(wurst.mods.flightMod.isEnabled())
			wurst.mods.flightMod.setEnabled(false);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.gameSettings.keyBindJump.pressed)
			mc.thePlayer.jump();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
