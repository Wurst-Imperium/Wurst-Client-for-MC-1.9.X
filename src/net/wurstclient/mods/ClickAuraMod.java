/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.mods.Mod.Bypasses;
import net.wurstclient.navigator.NavigatorItem;
import net.wurstclient.navigator.settings.CheckboxSetting;
import net.wurstclient.navigator.settings.SliderSetting;
import net.wurstclient.navigator.settings.SliderSetting.ValueDisplay;
import net.wurstclient.special.YesCheatSpf.BypassLevel;
import net.wurstclient.utils.EntityUtils;

@Mod.Info(category = Mod.Category.COMBAT,
	description = "Automatically attacks the closest valid entity whenever you\n"
		+ "click.\n"
		+ "Warning: ClickAuras generally look more suspicious than Killauras\n"
		+ "and are easier to detect. It is recommended to use Killaura or\n"
		+ "TriggerBot instead.",
	name = "ClickAura",
	tags = "Click Aura,ClickAimbot,Click Aimbot",
	help = "Mods/ClickAura")
@Bypasses(ghostMode = false)
public class ClickAuraMod extends Mod implements UpdateListener
{
	public CheckboxSetting useKillaura = new CheckboxSetting(
		"Use Killaura settings", true)
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
				fov.lockToValue(killaura.fov.getValue());
				hitThroughWalls.lock(killaura.hitThroughWalls.isChecked());
			}else
			{
				useCooldown.unlock();
				speed.unlock();
				range.unlock();
				fov.unlock();
				hitThroughWalls.unlock();
			}
		};
	};
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
		settings.add(useKillaura);
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
			wurst.mods.killauraMod, wurst.mods.killauraLegitMod,
			wurst.mods.multiAuraMod, wurst.mods.triggerBotMod};
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
		if(wurst.mods.triggerBotMod.isEnabled())
			wurst.mods.triggerBotMod.setEnabled(false);
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		EntityLivingBase en =
			EntityUtils.getClosestEntity(true,
				wurst.mods.killauraMod.fov.getValueF(),
				wurst.mods.killauraMod.hitThroughWalls.isChecked());
		if(en == null
			|| mc.thePlayer.getDistanceToEntity(en) > wurst.mods.killauraMod.range
				.getValueF())
		{
			EntityUtils.lookChanged = false;
			return;
		}
		EntityUtils.lookChanged = true;
		if(mc.gameSettings.keyBindAttack.pressed
			&& (wurst.mods.killauraMod.useCooldown.isChecked() ? mc.thePlayer
				.getSwordCooldown(0F) >= 1F
				: hasTimePassedS(wurst.mods.killauraMod.speed.getValueF())))
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
