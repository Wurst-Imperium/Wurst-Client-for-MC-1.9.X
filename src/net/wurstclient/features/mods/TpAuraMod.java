/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.compatibility.WPlayer;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.RotationUtils;

@Mod.Info(
	description = "Automatically attacks the closest valid entity while teleporting around it.",
	name = "TP-Aura",
	tags = "TpAura, EnderAura, tp aura, ender aura",
	help = "Mods/TP-Aura")
@Mod.Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false)
public final class TpAuraMod extends Mod implements UpdateListener
{
	private Random random = new Random();
	
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
		settings.add(useKillaura);
		settings.add(useCooldown);
		settings.add(speed);
		settings.add(range);
		settings.add(fov);
		settings.add(hitThroughWalls);
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.special.targetSpf, wurst.mods.killauraMod,
			wurst.mods.killauraLegitMod, wurst.mods.multiAuraMod,
			wurst.mods.clickAuraMod, wurst.mods.triggerBotMod};
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
			return;
		
		if(hasTimePassedS(speed.getValueF()))
		{
			WMinecraft.getPlayer().setPosition(
				en.posX + random.nextInt(3) * 2 - 2, en.posY,
				en.posZ + random.nextInt(3) * 2 - 2);
			
			if(!useCooldown.isChecked() || WPlayer.getCooldown() >= 1F)
			{
				if(wurst.mods.autoSwordMod.isActive())
					AutoSwordMod.setSlot();
				wurst.mods.criticalsMod.doCritical();
				RotationUtils.faceEntityPacket(en);
				
				mc.playerController.attackEntity(WMinecraft.getPlayer(), en);
				WPlayer.swingArmClient();
				
				WMinecraft.getPlayer().resetSwordCooldown();
			}
			updateLastMS();
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
