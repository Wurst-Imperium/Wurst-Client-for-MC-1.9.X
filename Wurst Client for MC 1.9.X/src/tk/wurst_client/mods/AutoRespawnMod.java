/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.client.gui.GuiScreen;
import tk.wurst_client.events.listeners.DeathListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.COMBAT,
	description = "Automatically respawns you whenever you die.",
	name = "AutoRespawn",
	tags = "auto respawn",
	tutorial = "Mods/AutoRespawn")
public class AutoRespawnMod extends Mod implements DeathListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(DeathListener.class, this);
	}
	
	@Override
	public void onDeath()
	{
		mc.thePlayer.respawnPlayer();
		mc.displayGuiScreen((GuiScreen)null);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(DeathListener.class, this);
	}
}
