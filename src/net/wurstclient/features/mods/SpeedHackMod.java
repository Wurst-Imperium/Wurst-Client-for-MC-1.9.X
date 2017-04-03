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
import net.wurstclient.features.mods.Mod.Bypasses;

@Mod.Info(
	description = "Allows you to run roughly 2.5x faster than you would by\n"
		+ "sprinting and jumping.\n"
		+ "Notice: This mod was patched in NoCheat+ version 3.13.2. It will\n"
		+ "only bypass older versions of NoCheat+. Type \"/ncp version\" to\n"
		+ "check the NoCheat+ version of a server.",
	name = "SpeedHack",
	tags = "speed hack",
	help = "Mods/SpeedHack")
@Bypasses(ghostMode = false, latestNCP = false)
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
		if(WMinecraft.getPlayer().isSneaking()
			|| WMinecraft.getPlayer().moveForward == 0
				&& WMinecraft.getPlayer().moveStrafing == 0)
			return;
		
		// activate sprint if walking forward
		if(WMinecraft.getPlayer().moveForward > 0
			&& !WMinecraft.getPlayer().isCollidedHorizontally)
			WMinecraft.getPlayer().setSprinting(true);
		
		// activate mini jump if on ground
		if(WMinecraft.getPlayer().onGround)
		{
			WMinecraft.getPlayer().motionY += 0.1;
			WMinecraft.getPlayer().motionX *= 1.8;
			WMinecraft.getPlayer().motionZ *= 1.8;
			double currentSpeed =
				Math.sqrt(Math.pow(WMinecraft.getPlayer().motionX, 2)
					+ Math.pow(WMinecraft.getPlayer().motionZ, 2));
			
			// limit speed to highest value that works on NoCheat+ version
			// 3.13.0-BETA-sMD5NET-b878
			// UPDATE: Patched in NoCheat+ version 3.13.2-SNAPSHOT-sMD5NET-b888
			double maxSpeed = 0.66F;
			if(currentSpeed > maxSpeed)
			{
				WMinecraft.getPlayer().motionX =
					WMinecraft.getPlayer().motionX / currentSpeed * maxSpeed;
				WMinecraft.getPlayer().motionZ =
					WMinecraft.getPlayer().motionZ / currentSpeed * maxSpeed;
			}
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
