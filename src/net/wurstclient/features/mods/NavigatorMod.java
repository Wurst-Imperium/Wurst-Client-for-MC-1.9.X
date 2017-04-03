/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.navigator.gui.NavigatorMainScreen;

@Mod.Info(description = "",
	name = "Navigator",
	tags = "ClickGUI",
	help = "Mods/Navigator")
@Mod.Bypasses
@Mod.DontSaveState
public final class NavigatorMod extends Mod
{
	@Override
	public void onEnable()
	{
		if(!(mc.currentScreen instanceof NavigatorMainScreen))
			mc.displayGuiScreen(new NavigatorMainScreen());
		setEnabled(false);
	}
}
