/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Info(description = "Changes the speed of almost everything.\n"
	+ "Tip: Slow speeds make aiming easier and work well with\n" + "NoCheat+.",
	name = "Timer",
	help = "Mods/Timer")
@Mod.Bypasses(ghostMode = false)
public final class TimerMod extends Mod
{
	public final SliderSetting speed =
		new SliderSetting("Speed", 2, 0.1, 10, 0.1, ValueDisplay.DECIMAL);
	
	@Override
	public void initSettings()
	{
		settings.add(speed);
	}
}
