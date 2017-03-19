/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.MathHelper;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;
import net.wurstclient.features.special_features.YesCheatSpf.BypassLevel;
import net.wurstclient.settings.CheckboxSetting;

@Info(category = Category.MOVEMENT,
	description = "Eases the use of the Elytra.",
	name = "ExtraElytra",
	tags = "EasyElytra, extra elytra, easy elytra",
	help = "Mods/ExtraElytra")
@Bypasses
public class ExtraElytraMod extends Mod implements UpdateListener
{
	private CheckboxSetting instantFly =
		new CheckboxSetting("Instant fly", true);
	private CheckboxSetting easyFly = new CheckboxSetting("Easy fly", false);
	private CheckboxSetting stopInWater =
		new CheckboxSetting("Stop flying in water", true);
	
	@Override
	public void initSettings()
	{
		settings.add(instantFly);
		settings.add(easyFly);
		settings.add(stopInWater);
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		
		ItemStack chest =
			mc.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if(chest == null || chest.getItem() != Items.elytra)
			return;
		
		if(mc.thePlayer.isFlyingWithElytra())
		{
			if(stopInWater.isChecked() && mc.thePlayer.isInWater())
			{
				mc.thePlayer.sendQueue
					.addToSendQueue(new CPacketEntityAction(mc.thePlayer,
						CPacketEntityAction.Action.START_FALL_FLYING));
				return;
			}
			
			if(easyFly.isChecked())
			{
				if(mc.gameSettings.keyBindJump.pressed)
					mc.thePlayer.motionY += 0.08;
				else if(mc.gameSettings.keyBindSneak.pressed)
					mc.thePlayer.motionY -= 0.04;
				
				if(mc.gameSettings.keyBindForward.pressed
					&& mc.thePlayer.getPosition().getY() < 256)
				{
					float yaw = (float)Math.toRadians(mc.thePlayer.rotationYaw);
					mc.thePlayer.motionX -= MathHelper.sin(yaw) * 0.05F;
					mc.thePlayer.motionZ += MathHelper.cos(yaw) * 0.05F;
				}else if(mc.gameSettings.keyBindBack.pressed
					&& mc.thePlayer.getPosition().getY() < 256)
				{
					float yaw = (float)Math.toRadians(mc.thePlayer.rotationYaw);
					mc.thePlayer.motionX += MathHelper.sin(yaw) * 0.05F;
					mc.thePlayer.motionZ -= MathHelper.cos(yaw) * 0.05F;
				}
			}
		}else if(instantFly.isChecked() && ItemElytra.isBroken(chest)
			&& mc.gameSettings.keyBindJump.pressed)
		{
			if(hasTimePassedM(1000))
			{
				updateLastMS();
				mc.thePlayer.setJumping(false);
				mc.thePlayer.setSprinting(true);
				mc.thePlayer.jump();
			}
			mc.thePlayer.sendQueue.addToSendQueue(new CPacketEntityAction(
				mc.thePlayer, CPacketEntityAction.Action.START_FALL_FLYING));
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
			case ANTICHEAT:
			case OLDER_NCP:
			case LATEST_NCP:
			easyFly.unlock();
			break;
			case GHOST_MODE:
			easyFly.lock(false);
			break;
		}
	}
}
