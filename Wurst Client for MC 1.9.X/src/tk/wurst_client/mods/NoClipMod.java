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
	description = "Allows you to freely move through blocks.\n"
		+ "A block (e.g. sand) must fall on your head to activate it.\n"
		+ "Warning: You will take damage while moving through blocks!",
	name = "NoClip",
	tags = "no clip",
	tutorial = "Mods/NoClip")
public class NoClipMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		mc.thePlayer.noClip = true;
		mc.thePlayer.fallDistance = 0;
		mc.thePlayer.onGround = false;
		
		mc.thePlayer.capabilities.isFlying = false;
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionY = 0;
		mc.thePlayer.motionZ = 0;
		
		float speed = 0.2F;
		mc.thePlayer.jumpMovementFactor = speed;
		if(mc.gameSettings.keyBindJump.pressed)
			mc.thePlayer.motionY += speed;
		if(mc.gameSettings.keyBindSneak.pressed)
			mc.thePlayer.motionY -= speed;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.thePlayer.noClip = false;
	}
}
