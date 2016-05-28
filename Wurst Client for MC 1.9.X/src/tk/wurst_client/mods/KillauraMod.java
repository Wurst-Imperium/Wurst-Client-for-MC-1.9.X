/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.settings.CheckboxSetting;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.navigator.settings.SliderSetting.ValueDisplay;
import tk.wurst_client.special.YesCheatSpf.BypassLevel;
import tk.wurst_client.utils.EntityUtils;

@Info(category = Category.COMBAT,
	description = "Automatically attacks everything in your range.",
	name = "Killaura",
	tags = "kill aura",
	help = "Mods/Killaura")
@Bypasses
public class KillauraMod extends Mod implements UpdateListener
{
	public CheckboxSetting useCooldown = new CheckboxSetting(
		"Use Attack Cooldown as Speed", true)
	{
		@Override
		public void update()
		{
			speed.setDisabled(isChecked());
		};
	};
	public SliderSetting speed = new SliderSetting("Speed", 20, 2, 20, 0.1,
		ValueDisplay.DECIMAL);
	public SliderSetting range = new SliderSetting("Range", 6, 1, 6, 0.05,
		ValueDisplay.DECIMAL);
	public SliderSetting fov = new SliderSetting("FOV", 360, 30, 360, 10,
		ValueDisplay.DEGREES);
	public CheckboxSetting hitThroughWalls = new CheckboxSetting(
		"Hit through walls", false);
	
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
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.special.targetSpf,
			wurst.mods.killauraLegitMod, wurst.mods.multiAuraMod,
			wurst.mods.clickAuraMod, wurst.mods.triggerBotMod,
			wurst.mods.criticalsMod};
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
		EntityLivingBase en =
			EntityUtils.getClosestEntity(true, fov.getValueF(),
				hitThroughWalls.isChecked());
		if(en == null
			|| mc.thePlayer.getDistanceToEntity(en) > range.getValueF())
		{
			EntityUtils.lookChanged = false;
			return;
		}
		EntityUtils.lookChanged = true;
		if((useCooldown.isChecked() ? mc.thePlayer.getSwordCooldown(0F) >= 1F
			: hasTimePassedS(speed.getValueF())))
		{
			if(wurst.mods.autoSwordMod.isActive())
				AutoSwordMod.setSlot();
			wurst.mods.criticalsMod.doCritical();
			wurst.mods.blockHitMod.doBlock();
			
			if(EntityUtils.faceEntityPacket(en))
			{
				mc.playerController.attackEntity(mc.thePlayer, en);
				mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
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
			case MINEPLEX_ANTICHEAT:
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
