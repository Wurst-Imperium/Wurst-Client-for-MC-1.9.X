/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;

@Mod.Info(description = "Automatically attacks the entity you're looking at.",
	name = "TriggerBot",
	tags = "trigger bot",
	help = "Mods/TriggerBot")
@Mod.Bypasses
public final class TriggerBotMod extends Mod implements UpdateListener
{
	public CheckboxSetting useKillaura =
		new CheckboxSetting("Use Killaura settings", true)
		{
			@Override
			public void update()
			{
				if(isChecked())
				{
					KillauraMod killaura = wurst.mods.killauraMod;
					
					if(useCooldown != null)
						useCooldown.lock(killaura.useCooldown);
					
					speed.lock(killaura.speed);
					range.lock(killaura.range);
				}else
				{
					if(useCooldown != null)
						useCooldown.unlock();
					
					speed.unlock();
					range.unlock();
				}
			}
		};
	public CheckboxSetting useCooldown = !WMinecraft.COOLDOWN ? null
		: new CheckboxSetting("Use Attack Cooldown as Speed", true)
		{
			@Override
			public void update()
			{
				speed.setDisabled(isChecked());
			}
		};
	public SliderSetting speed =
		new SliderSetting("Speed", 20, 0.1, 20, 0.1, ValueDisplay.DECIMAL);
	public SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public float getRange()
		{
			return range.getValueF();
		}
	};
	
	@Override
	public void initSettings()
	{
		settings.add(useKillaura);
		
		if(useCooldown != null)
			settings.add(useCooldown);
		
		settings.add(speed);
		settings.add(range);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.targetSpf, wurst.mods.killauraMod,
			wurst.mods.killauraLegitMod, wurst.mods.multiAuraMod,
			wurst.mods.clickAuraMod, wurst.mods.criticalsMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable other killauras
		wurst.mods.killauraMod.setEnabled(false);
		wurst.mods.killauraLegitMod.setEnabled(false);
		wurst.mods.multiAuraMod.setEnabled(false);
		wurst.mods.clickAuraMod.setEnabled(false);
		wurst.mods.tpAuraMod.setEnabled(false);
		
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// update timer
		updateMS();
		
		// check timer / cooldown
		if(useCooldown != null && useCooldown.isChecked()
			? WPlayer.getCooldown() < 1 : !hasTimePassedS(speed.getValueF()))
			return;
		
		// check entity
		if(mc.objectMouseOver == null || !EntityUtils
			.isCorrectEntity(mc.objectMouseOver.entityHit, targetSettings))
			return;
		
		// prepare attack
		EntityUtils.prepareAttack();
		
		// attack entity
		EntityUtils.attackEntity(mc.objectMouseOver.entityHit);
		
		// reset timer
		updateLastMS();
	}
	
	@Override
	public void onYesCheatUpdate(BypassLevel bypassLevel)
	{
		switch(bypassLevel)
		{
			default:
			case OFF:
			case MINEPLEX:
			speed.resetUsableMax();
			range.resetUsableMax();
			break;
			
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			case GHOST_MODE:
			speed.setUsableMax(12);
			range.setUsableMax(4.25);
			break;
		}
	}
}
