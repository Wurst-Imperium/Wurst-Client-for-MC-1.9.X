/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.util.text.ITextComponent;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.BLOCKS,
	description = "Instantly writes whatever text you want on every sign\n"
		+ "you place. Once activated, you can write normally on\n"
		+ "one sign to specify the text for all other signs.",
	name = "AutoSign",
	noCheatCompatible = false)
public class AutoSignMod extends Mod
{
	public ITextComponent[] signText;
	
	@Override
	public void onEnable()
	{
		signText = null;
	}
}
