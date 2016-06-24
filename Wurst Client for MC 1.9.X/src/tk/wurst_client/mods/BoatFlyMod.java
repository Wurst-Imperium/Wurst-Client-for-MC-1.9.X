/*
 * Copyright © 2016 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Bypasses;

@Mod.Info(category = Mod.Category.MOVEMENT,
	description = "Allows you to fly with boats and rideable entities.\n"
		+ "Bypasses NoCheat+, at least for now.",
	name = "BoatFly",
	tags = "BoatFlight, boat fly, boat flight",
	help = "Mods/BoatFly")
@Bypasses(ghostMode = false, latestNCP = false)
public class BoatFlyMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(!mc.thePlayer.isRiding())
			return;
		
		// Might want to add a option for boats gliding down (motionY -0.035), but for now I will leave this as is.
		mc.thePlayer.getRidingEntity().motionY =
			mc.gameSettings.keyBindJump.pressed ? 0.3 : -0.035;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
