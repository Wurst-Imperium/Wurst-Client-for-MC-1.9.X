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
import net.wurstclient.navigator.settings.SliderSetting;
import net.wurstclient.navigator.settings.SliderSetting.ValueDisplay;

@Info(category = Category.MOVEMENT,
	description = "Changes the speed of almost everything.\n"
		+ "Tip: Slow speeds make aiming easier and work well with\n"
		+ "NoCheat+.",
	name = "Timer",
	help = "Mods/Timer")
@Bypasses(ghostMode = false)
public class TimerMod extends Mod
{
	public float speed = 2.0F;
	
	@Override
	public void initSettings()
	{
		settings.add(new SliderSetting("Speed", speed, 0.1, 10, 0.1,
			ValueDisplay.DECIMAL)
		{
			@Override
			public void update()
			{
				speed = (float)getValue();
			}
		});
	}
}
