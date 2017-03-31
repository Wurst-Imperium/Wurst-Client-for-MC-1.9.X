/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult.Type;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityUtils;

@Info(category = Category.COMBAT,
	description = "Automatically attacks the entity you're looking at.",
	name = "TriggerBot",
	tags = "trigger bot",
	help = "Mods/TriggerBot")
@Bypasses
public class TriggerBotMod extends Mod implements UpdateListener
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
					useCooldown.lock(killaura.useCooldown.isChecked());
					speed.lockToValue(killaura.speed.getValue());
					range.lockToValue(killaura.range.getValue());
				}else
				{
					useCooldown.unlock();
					speed.unlock();
					range.unlock();
				}
			};
		};
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
	
	@Override
	public void initSettings()
	{
		settings.add(useKillaura);
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
		// TODO: Clean up this mess!
		if(wurst.mods.killauraMod.isEnabled())
			wurst.mods.killauraMod.setEnabled(false);
		if(wurst.mods.killauraLegitMod.isEnabled())
			wurst.mods.killauraLegitMod.setEnabled(false);
		if(wurst.mods.multiAuraMod.isEnabled())
			wurst.mods.multiAuraMod.setEnabled(false);
		if(wurst.mods.clickAuraMod.isEnabled())
			wurst.mods.clickAuraMod.setEnabled(false);
		if(wurst.mods.tpAuraMod.isEnabled())
			wurst.mods.tpAuraMod.setEnabled(false);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		
		if(mc.objectMouseOver == null
			|| mc.objectMouseOver.typeOfHit != Type.ENTITY
			|| !(mc.objectMouseOver.entityHit instanceof EntityLivingBase))
			return;
		EntityLivingBase en = (EntityLivingBase)mc.objectMouseOver.entityHit;
		
		if((useCooldown.isChecked() ? mc.thePlayer.getSwordCooldown(0F) >= 1F
			: hasTimePassedS(speed.getValueF()))
			&& mc.thePlayer.getDistanceToEntity(en) <= range.getValueF()
			&& EntityUtils.isCorrectEntity(en, true))
		{
			if(wurst.mods.autoSwordMod.isActive())
				AutoSwordMod.setSlot();
			wurst.mods.criticalsMod.doCritical();
			wurst.mods.blockHitMod.doBlock();
			
			mc.playerController.attackEntity(mc.thePlayer, en);
			mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
			
			updateLastMS();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
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
			break;
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			speed.lockToMax(12);
			range.lockToMax(4.25);
			break;
			case GHOST_MODE:
			speed.lockToMax(12);
			range.lockToMax(4.25);
			break;
		}
	}
}
