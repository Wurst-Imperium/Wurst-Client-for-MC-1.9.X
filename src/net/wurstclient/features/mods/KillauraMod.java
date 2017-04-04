/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.entity.EntityLivingBase;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityUtils;

@Mod.Info(description = "Automatically attacks everything in your range.",
	name = "Killaura",
	tags = "kill aura",
	help = "Mods/Killaura")
@Mod.Bypasses
public final class KillauraMod extends Mod implements UpdateListener
{
	public CheckboxSetting useCooldown =
		new CheckboxSetting("Use Attack Cooldown as Speed", true)
		{
			@Override
			public void update()
			{
				speed.setDisabled(isChecked());
			};
		};
	public SliderSetting speed =
		new SliderSetting("Speed", 20, 2, 20, 0.1, ValueDisplay.DECIMAL);
	public SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	public SliderSetting fov =
		new SliderSetting("FOV", 360, 30, 360, 10, ValueDisplay.DEGREES);
	public CheckboxSetting hitThroughWalls =
		new CheckboxSetting("Hit through walls", false);
	
	@Override
	public void initSettings()
	{
		settings.add(useCooldown);
		settings.add(speed);
		settings.add(range);
		settings.add(fov);
		settings.add(hitThroughWalls);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.targetSpf,
			wurst.mods.killauraLegitMod, wurst.mods.multiAuraMod,
			wurst.mods.clickAuraMod, wurst.mods.tpAuraMod,
			wurst.mods.triggerBotMod, wurst.mods.criticalsMod};
	}
	
	@Override
	public void onEnable()
	{
		// TODO: Clean up this mess!
		if(wurst.mods.killauraLegitMod.isEnabled())
			wurst.mods.killauraLegitMod.setEnabled(false);
		if(wurst.mods.multiAuraMod.isEnabled())
			wurst.mods.multiAuraMod.setEnabled(false);
		if(wurst.mods.clickAuraMod.isEnabled())
			wurst.mods.clickAuraMod.setEnabled(false);
		if(wurst.mods.tpAuraMod.isEnabled())
			wurst.mods.tpAuraMod.setEnabled(false);
		if(wurst.mods.triggerBotMod.isEnabled())
			wurst.mods.triggerBotMod.setEnabled(false);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		EntityLivingBase en = EntityUtils.getClosestEntity(true,
			fov.getValueF(), hitThroughWalls.isChecked());
		if(en == null || WMinecraft.getPlayer().getDistanceToEntity(en) > range
			.getValueF())
		{
			EntityUtils.lookChanged = false;
			return;
		}
		EntityUtils.lookChanged = true;
		if(useCooldown.isChecked() ? WPlayer.getCooldown() >= 1F
			: hasTimePassedS(speed.getValueF()))
		{
			if(wurst.mods.autoSwordMod.isActive())
				AutoSwordMod.setSlot();
			wurst.mods.criticalsMod.doCritical();
			
			if(EntityUtils.faceEntityPacket(en))
			{
				mc.playerController.attackEntity(WMinecraft.getPlayer(), en);
				WPlayer.swingArmClient();
			}
			
			updateLastMS();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		EntityUtils.lookChanged = false;
	}
	
	@Override
	public void onYesCheatUpdate(BypassLevel bypassLevel)
	{
		switch(bypassLevel)
		{
			default:
			case OFF:
			case MINEPLEX:
			speed.unlock();
			range.unlock();
			hitThroughWalls.unlock();
			break;
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			speed.lockToMax(12);
			range.lockToMax(4.25);
			hitThroughWalls.unlock();
			break;
			case GHOST_MODE:
			speed.lockToMax(12);
			range.lockToMax(4.25);
			hitThroughWalls.lock(false);
			break;
		}
	}
}
