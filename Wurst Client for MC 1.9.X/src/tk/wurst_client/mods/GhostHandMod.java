/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

@Mod.Info(category = Mod.Category.BLOCKS,
	description = "Allows you to reach specific blocks through walls.\n"
		+ "Use .ghosthand id <block id> or .ghosthand name <block name>\n"
		+ "to specify it.",
	name = "GhostHand",
	tags = "ghost hand",
	tutorial = "Mods/GhostHand")
public class GhostHandMod extends Mod
{
	@Override
	public String getRenderName()
	{
		return getName() + " [" + wurst.options.ghostHandID + "]";
	}
}
