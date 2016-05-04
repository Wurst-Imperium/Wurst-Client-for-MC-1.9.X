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

@Info(category = Category.EXPLOITS,
	description = "Makes you invisible and invincible.\n"
		+ "If you die and respawn near a certain player while\n"
		+ "this mod is enabled, that player will be unable to see\n"
		+ "you. Only works on vanilla servers!",
	name = "Invisibility",
	noCheatCompatible = false,
	tags = "Invisible, GodMode, god mode",
	tutorial = "Mods/Invisibility")
public class InvisibilityMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.thePlayer.getHealth() <= 0)
			if(isEnabled())
			{
				// Respawning too early for server-side invisibility
				mc.thePlayer.respawnPlayer();
				wurst.chat.message("You should now be invisible.");
			}else
			{
				wurst.chat.message("You are no longer invisible.");
				wurst.events.remove(UpdateListener.class, this);
			}
	}
}
