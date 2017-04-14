/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import org.lwjgl.input.Keyboard;

import net.wurstclient.events.listeners.UpdateListener;

@Mod.Info(description = "Automatically walks all the time.",
	name = "AutoWalk",
	tags = "auto walk",
	help = "Mods/AutoWalk")
@Mod.Bypasses
public final class AutoWalkMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		
		// reset forward key
		mc.gameSettings.keyBindForward.pressed =
			Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
	}
	
	@Override
	public void onUpdate()
	{
		// force-press forward key
		mc.gameSettings.keyBindForward.pressed = true;
	}
}
