/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.GoRandomAI;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;

@Mod.Info(name = "AntiAFK",
	description = "Walks around randomly to hide you from AFK detectors.",
	tags = "AFKBot,anti afk,afk bot",
	help = "Mods/AntiAFK")
@Mod.Bypasses(ghostMode = false)
@Mod.DontSaveState
public final class AntiAfkMod extends Mod implements UpdateListener
{
	private GoRandomAI ai;
	private int timer;
	private Random random = new Random();
	
	@Override
	public void onEnable()
	{
		ai = new GoRandomAI(new BlockPos(WMinecraft.getPlayer()), 16F);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		
		if(ai != null)
			ai.stop();
	}
	
	@Override
	public void onUpdate()
	{
		// check if player died
		if(WMinecraft.getPlayer().getHealth() <= 0)
		{
			setEnabled(false);
			return;
		}
		
		// update timer
		if(timer > 0)
		{
			timer--;
			mc.gameSettings.keyBindJump.pressed =
				WMinecraft.getPlayer().isInWater();
			return;
		}
		
		// walk around
		ai.update();
		
		// wait 2 - 3 seconds (40 - 60 ticks)
		if(ai.isDone())
		{
			ai.stop();
			timer = 40 + random.nextInt(21);
		}
	}
}
