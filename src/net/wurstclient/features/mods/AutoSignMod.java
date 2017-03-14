/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.util.text.ITextComponent;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;

@Info(category = Category.BLOCKS,
	description = "Instantly writes whatever text you want on every sign\n"
		+ "you place. Once activated, you can write normally on\n"
		+ "one sign to specify the text for all other signs.",
	name = "AutoSign",
	noCheatCompatible = false,
	tags = "auto sign",
	help = "Mods/AutoSign")
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false, antiCheat = false)
public class AutoSignMod extends Mod
{
	public ITextComponent[] signText;
	
	@Override
	public void onEnable()
	{
		signText = null;
	}
}
