/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;

@Info(category = Category.RENDER,
	description = "Changes the scale of the nametags so you can always read them.\n"
		+ "Also allows you to see the nametags of sneaking players.",
	name = "NameTags",
	tags = "name tags",
	help = "Mods/NameTags")
@Bypasses
public class NameTagsMod extends Mod
{
	
}
