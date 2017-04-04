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
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.RotationUtils;

@Mod.Info(
	description = "A bot that automatically fights for you.\n"
		+ "It walks around and kills everything.\n" + "Good for MobArena.",
	name = "FightBot",
	tags = "fight bot",
	help = "Mods/FightBot")
@Mod.Bypasses(ghostMode = false)
@Mod.DontSaveState
public final class FightBotMod extends Mod implements UpdateListener
{
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
		entity = EntityUtils.getClosestEntity(true, 360, false);
		if(entity == null)
			return;
		if(entity.getHealth() <= 0 || entity.isDead
			|| WMinecraft.getPlayer().getHealth() <= 0)
		{
			entity = null;
			mc.gameSettings.keyBindForward.pressed = false;
			return;
		}
		double xDist = Math.abs(WMinecraft.getPlayer().posX - entity.posX);
		double zDist = Math.abs(WMinecraft.getPlayer().posZ - entity.posZ);
		RotationUtils.faceEntityClient(entity);
		if(xDist > distance || zDist > distance)
			mc.gameSettings.keyBindForward.pressed = true;
		else
			mc.gameSettings.keyBindForward.pressed = false;
		if(WMinecraft.getPlayer().isCollidedHorizontally
			&& WMinecraft.getPlayer().onGround)
			WMinecraft.getPlayer().jump();
		if(WMinecraft.getPlayer().isInWater()
			&& WMinecraft.getPlayer().posY < entity.posY)
			WMinecraft.getPlayer().motionY += 0.04;
		updateMS();
		if(hasTimePassedS(wurst.mods.killauraMod.speed.getValueF()))
			if(WMinecraft.getPlayer().getDistanceToEntity(entity) <= range)
			{
				if(wurst.mods.autoSwordMod.isActive())
					AutoSwordMod.setSlot();
				wurst.mods.criticalsMod.doCritical();
				if(RotationUtils.getAngleToClientRotation(
					entity.boundingBox.getCenter()) > 55)
					RotationUtils.faceEntityClient(entity);
				else
				{
					RotationUtils.faceEntityClient(entity);
					WPlayer.swingArmClient();
					mc.playerController.attackEntity(WMinecraft.getPlayer(),
						entity);
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
