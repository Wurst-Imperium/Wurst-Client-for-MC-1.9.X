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
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;

@Info(category = Category.MISC,
	description = "Uses an item multiple times.\n"
		+ "This can cause a lot of lag and even crash a server.\n"
		+ "Works best with snowballs or eggs.\n"
		+ "Use the .throw command to change the amount of uses per click.",
	name = "Throw",
	help = "Mods/Throw")
@Bypasses(ghostMode = false)
public class ThrowMod extends Mod implements UpdateListener
{
	@Override
	public String getRenderName()
	{
		return getName() + " [" + wurst.options.throwAmount + "]";
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if((mc.rightClickDelayTimer == 4 || wurst.mods.fastPlaceMod.isActive())
			&& mc.gameSettings.keyBindUseItem.pressed)
		{
			if(mc.objectMouseOver == null
				|| WMinecraft.getPlayer().inventory.getCurrentItem() == null)
				return;
			for(int i = 0; i < wurst.options.throwAmount - 1; i++)
				mc.rightClickMouse();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
