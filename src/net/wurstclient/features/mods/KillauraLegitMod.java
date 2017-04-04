/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;
import net.wurstclient.utils.RotationUtils;

@Mod.Info(
	description = "Slower Killaura that bypasses any AntiCheat plugins.\n"
		+ "Not required on normal NoCheat+ servers!",
	name = "KillauraLegit",
	tags = "LegitAura, killaura legit, kill aura legit, legit aura",
	help = "Mods/KillauraLegit")
@Mod.Bypasses
public final class KillauraLegitMod extends Mod implements UpdateListener
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
					fov.lock(killaura.fov);
				}else
				{
					if(useCooldown != null)
						useCooldown.unlock();
					
					speed.unlock();
					range.unlock();
					fov.unlock();
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
		new SliderSetting("Speed", 12, 0.1, 12, 0.1, ValueDisplay.DECIMAL);
	public SliderSetting range =
		new SliderSetting("Range", 4.25, 1, 4.25, 0.05, ValueDisplay.DECIMAL);
	public SliderSetting fov =
		new SliderSetting("FOV", 360, 30, 360, 10, ValueDisplay.DEGREES);
	
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public float getRange()
		{
			return range.getValueF();
		}
		
		@Override
		public float getFOV()
		{
			return fov.getValueF();
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
		settings.add(fov);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.targetSpf, wurst.mods.killauraMod,
			wurst.mods.multiAuraMod, wurst.mods.clickAuraMod,
			wurst.mods.triggerBotMod};
	}
	
	@Override
	public void onEnable()
	{
		// disable other killauras
		wurst.mods.killauraMod.setEnabled(false);
		wurst.mods.multiAuraMod.setEnabled(false);
		wurst.mods.clickAuraMod.setEnabled(false);
		wurst.mods.tpAuraMod.setEnabled(false);
		wurst.mods.triggerBotMod.setEnabled(false);
		
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
		
		// set entity
		Entity entity = EntityUtils.getBestEntityToAttack(targetSettings);
		if(entity == null)
			return;
		
		// face entity
		if(!RotationUtils.faceEntityClient(entity))
			return;
		
		// Criticals
		if(wurst.mods.criticalsMod.isActive()
			&& WMinecraft.getPlayer().onGround)
			WMinecraft.getPlayer().jump();
		
		// attack entity
		EntityUtils.attackEntity(entity);
		
		// reset timer
		updateLastMS();
	}
}
