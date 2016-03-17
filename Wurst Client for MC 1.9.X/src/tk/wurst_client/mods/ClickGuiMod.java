/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import org.darkstorm.minecraft.gui.util.GuiManagerDisplayScreen;

import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.HIDDEN, description = "", name = "ClickGUI")
public class ClickGuiMod extends Mod implements UpdateListener
{
	public ClickGuiMod()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onToggle()
	{
		if(!(mc.currentScreen instanceof GuiManagerDisplayScreen))
			mc.displayGuiScreen(new GuiManagerDisplayScreen(wurst.gui));
	}
	
	@Override
	public void onUpdate()
	{
		wurst.gui.update();
	}
}
