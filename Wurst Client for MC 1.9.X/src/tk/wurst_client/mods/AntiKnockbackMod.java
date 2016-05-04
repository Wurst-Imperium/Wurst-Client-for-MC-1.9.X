/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import org.darkstorm.minecraft.gui.component.BoundedRangeComponent.ValueDisplay;

import tk.wurst_client.navigator.settings.SliderSetting;

@Mod.Info(category = Mod.Category.COMBAT,
	description = "Protects you from getting pushed by players, mobs and\n"
		+ "fluids.",
	name = "AntiKnockback",
	tags = "AntiVelocity, NoKnockback, AntiKB, anti knockback, anti velocity, no knockback, anti kb",
	tutorial = "Mods/AntiKnockback")
public class AntiKnockbackMod extends Mod
{
	public float strength = 1F;
	
	@Override
	public void initSettings()
	{
		settings.add(new SliderSetting("Strength", strength, 0.01, 1, 0.01,
			ValueDisplay.PERCENTAGE)
		{
			@Override
			public void update()
			{
				strength = (float)getValue();
			}
		});
	}
}
