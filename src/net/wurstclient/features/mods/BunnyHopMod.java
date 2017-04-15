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
	description = "Automatically jumps whenever you walk.\n"
		+ "Tip: Jumping while sprinting is a faster way to move.",
	name = "BunnyHop",
	tags = "AutoJump, BHop, bunny hop, auto jump",
	help = "Mods/BunnyHop")
@Mod.Bypasses
public final class BunnyHopMod extends Mod implements UpdateListener
{
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoSprintMod, wurst.mods.highJumpMod,
			wurst.commands.jumpCmd};
	}
	
	@Override
	public void onEnable()
	{
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
		// check onGround
		if(!WMinecraft.getPlayer().onGround)
			return;
		
		// check if sneaking
		if(WMinecraft.getPlayer().isSneaking())
			return;
		
		// check if moving
		if(WMinecraft.getPlayer().moveForward == 0
			&& WMinecraft.getPlayer().moveStrafing == 0)
			return;
		
		// jump
		WMinecraft.getPlayer().jump();
	}
}
