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

@Mod.Info(
	description = "Protects you from getting pushed by players, mobs and fluids.",
	name = "AntiKnockback",
	tags = "AntiVelocity, NoKnockback, AntiKB, anti knockback, anti velocity, no knockback, anti kb",
	help = "Mods/AntiKnockback")
@Mod.Bypasses(ghostMode = false)
public final class AntiKnockbackMod extends Mod
{
	public final SliderSetting strength = new SliderSetting("Strength", 1, 0.01,
		1, 0.01, ValueDisplay.PERCENTAGE);
	
	@Override
	public void initSettings()
	{
		settings.add(strength);
	}
}
