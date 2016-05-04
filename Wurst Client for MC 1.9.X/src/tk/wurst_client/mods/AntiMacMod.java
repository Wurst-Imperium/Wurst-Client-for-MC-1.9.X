/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.util.HashSet;

import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.MISC,
	description = "Makes other mods bypass Mineplex AntiCheat or blocks them\n"
		+ "if they can't.",
	name = "AntiMAC",
	tags = "anti mac, Mineplex AntiCheat, mineplex anti cheat, AntiMineplex, anti mineplex",
	tutorial = "Mods/AntiMAC")
public class AntiMacMod extends Mod
{
	private HashSet<Mod> blockedMods;
	
	@Override
	public void onEnable()
	{
		if(wurst.mods.yesCheatMod.isEnabled())
			wurst.mods.yesCheatMod.setEnabled(false);
		if(blockedMods == null)
		{
			blockedMods = new HashSet<>();
			// add mods that down't work with YesCheat+
			for(Mod mod : wurst.mods.getAllMods())
				if(!mod.getClass().getAnnotation(Mod.Info.class)
					.noCheatCompatible())
					blockedMods.add(mod);
			
			// remove mods that work with MAC
			// TODO: More efficient method to do this
			blockedMods.remove(wurst.mods.antiFireMod);
			blockedMods.remove(wurst.mods.antiPotionMod);
			blockedMods.remove(wurst.mods.fastBowMod);
			blockedMods.remove(wurst.mods.glideMod);
			blockedMods.remove(wurst.mods.multiAuraMod);
			blockedMods.remove(wurst.mods.noSlowdownMod);
			blockedMods.remove(wurst.mods.regenMod);
			blockedMods.remove(wurst.mods.spiderMod);
			blockedMods.remove(wurst.mods.tpAuraMod);
			
			// block FancyChat because Mineplex disables special characters
			blockedMods.add(wurst.mods.fancyChatMod);
		}
		for(Mod mod : blockedMods)
			mod.setBlocked(true);
	}
	
	@Override
	public void onDisable()
	{
		for(Mod mod : blockedMods)
			mod.setBlocked(false);
	}
}
