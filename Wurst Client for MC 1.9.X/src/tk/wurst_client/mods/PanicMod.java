/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | A few rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.MISC,
	description = "Instantly turns off all enabled mods.\n"
		+ "Be careful with this!",
	name = "Panic",
	tags = "legit, disable",
	help = "Mods/Panic")
public class PanicMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	private Mod panicedMods[] = [];
	
	@Override
	public void onUpdate()
	{
		for(Mod mod : wurst.mods.getAllMods())
			if(mod.getCategory() != Category.HIDDEN && mod.isEnabled())
				mod.setEnabled(false);
				panicedMods.add(mod);
	}
	
	@Override
	public void onDisable()
	{
		for(Mod mod : wurst.mods.getAllMods())
		{//I like curly braces :}
			mod.setEnabled(true);
			panicedMods.add(mod);
		}
		wurst.events.remove(UpdateListener.class, this);
	}
}
