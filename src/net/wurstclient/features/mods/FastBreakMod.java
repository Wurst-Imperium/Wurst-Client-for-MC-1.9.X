/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.features.Feature;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@Mod.Info(
	description = "Allows you to break blocks faster.\n"
		+ "Tip: This works with Nuker.",
	name = "FastBreak",
	tags = "SpeedMine, SpeedyGonzales, fast break, speed mine, speedy gonzales",
	help = "Mods/FastBreak")
@Mod.Bypasses
public final class FastBreakMod extends Mod
{
	public final ModeSetting mode =
		new ModeSetting("Mode", new String[]{"Normal", "Instant"}, 1)
		{
			@Override
			public void update()
			{
				speed.setDisabled(getSelected() == 1);
			}
		};
	public final SliderSetting speed =
		new SliderSetting("Speed", 2, 1, 5, 0.05, ValueDisplay.DECIMAL);
	
	@Override
	public void initSettings()
	{
		settings.add(mode);
		settings.add(speed);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.fastPlaceMod, wurst.mods.autoMineMod,
			wurst.mods.nukerMod};
	}
	
	public float getHardnessModifier()
	{
		if(!isActive())
			return 1;
		
		if(mode.getSelected() != 0)
			return 1;
		
		return speed.getValueF();
	}
	
	public boolean shouldSpamPackets()
	{
		return isActive() && mode.getSelected() == 1;
	}
}
