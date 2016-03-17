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
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.utils.EntityUtils;

@Info(category = Category.COMBAT,
	description = "A bot that automatically fights for you.\n"
		+ "It walks around and kills everything.\n" + "Good for MobArena.",
	name = "FightBot")
public class FightBotMod extends Mod implements UpdateListener
{
	private float speed;
	private float range = 6F;
	private double distance = 3D;
	private EntityLivingBase entity;
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		entity = EntityUtils.getClosestEntity(true, false);
		if(entity == null)
			return;
		if(entity.getHealth() <= 0 || entity.isDead
			|| mc.thePlayer.getHealth() <= 0)
		{
			entity = null;
			mc.gameSettings.keyBindForward.pressed = false;
			return;
		}
		double xDist = Math.abs(mc.thePlayer.posX - entity.posX);
		double zDist = Math.abs(mc.thePlayer.posZ - entity.posZ);
		EntityUtils.faceEntityClient(entity);
		if(xDist > distance || zDist > distance)
			mc.gameSettings.keyBindForward.pressed = true;
		else
			mc.gameSettings.keyBindForward.pressed = false;
		if(mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround)
			mc.thePlayer.jump();
		if(mc.thePlayer.isInWater() && mc.thePlayer.posY < entity.posY)
			mc.thePlayer.motionY += 0.04;
		if(wurst.mods.yesCheatMod.isActive())
			speed = wurst.mods.killauraMod.yesCheatSpeed;
		else
			speed = wurst.mods.killauraMod.normalSpeed;
		updateMS();
		if(hasTimePassedS(speed))
			if(mc.thePlayer.getDistanceToEntity(entity) <= range)
			{
				if(wurst.mods.autoSwordMod.isActive())
					AutoSwordMod.setSlot();
				wurst.mods.criticalsMod.doCritical();
				wurst.mods.blockHitMod.doBlock();
				if(EntityUtils.getDistanceFromMouse(entity) > 55)
					EntityUtils.faceEntityClient(entity);
				else
				{
					EntityUtils.faceEntityClient(entity);
					mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
					mc.playerController.attackEntity(mc.thePlayer, entity);
				}
				updateLastMS();
			}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		mc.gameSettings.keyBindForward.pressed = false;
	}
}
