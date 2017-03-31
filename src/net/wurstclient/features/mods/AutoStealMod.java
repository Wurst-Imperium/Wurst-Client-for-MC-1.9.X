/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;

@Info(category = Category.MISC,
	description = "Automatically steals everything from all chests you\n"
		+ "open.",
	name = "AutoSteal",
	tags = "auto steal",
	help = "Mods/AutoSteal")
@Bypasses
public class AutoStealMod extends Mod
{
	
}
