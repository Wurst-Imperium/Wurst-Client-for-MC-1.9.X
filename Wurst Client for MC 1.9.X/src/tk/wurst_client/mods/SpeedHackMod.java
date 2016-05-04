/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import tk.wurst_client.events.listeners.UpdateListener;

@Mod.Info(category = Mod.Category.MOVEMENT,
	description = "Allows you to run roughly 2.5x faster than you would by\n"
		+ "sprinting and jumping.\n"
		+ "Notice: This mod was patched in NoCheat+ version 3.13.2. It will\n"
		+ "only bypass older versions of NoCheat+. Type \"/ncp version\" to\n"
		+ "check the NoCheat+ version of a server.",
	name = "SpeedHack",
	tags = "speed hack",
	tutorial = "Mods/SpeedHack")
public class SpeedHackMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// return if sneaking or not walking
		if(mc.thePlayer.isSneaking() || mc.thePlayer.moveForward == 0
			&& mc.thePlayer.moveStrafing == 0)
			return;
		
		// activate sprint if walking forward
		if(mc.thePlayer.moveForward > 0 && !mc.thePlayer.isCollidedHorizontally)
			mc.thePlayer.setSprinting(true);
		
		// activate mini jump if on ground
		if(mc.thePlayer.onGround)
		{
			mc.thePlayer.motionY += 0.1;
			mc.thePlayer.motionX *= 1.8;
			mc.thePlayer.motionZ *= 1.8;
			double currentSpeed =
				Math.sqrt(Math.pow(mc.thePlayer.motionX, 2)
					+ Math.pow(mc.thePlayer.motionZ, 2));
			
			// limit speed to highest value that works on NoCheat+ version
			// 3.13.0-BETA-sMD5NET-b878
			// UPDATE: Patched in NoCheat+ version 3.13.2-SNAPSHOT-sMD5NET-b888
			double maxSpeed = 0.66F;
			if(currentSpeed > maxSpeed)
			{
				mc.thePlayer.motionX =
					mc.thePlayer.motionX / currentSpeed * maxSpeed;
				mc.thePlayer.motionZ =
					mc.thePlayer.motionZ / currentSpeed * maxSpeed;
			}
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
