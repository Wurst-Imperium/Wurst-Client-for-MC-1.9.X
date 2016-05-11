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
	description = "A bot that follows the closest entity and protects it.",
	name = "Protect",
	tutorial = "Mods/Protect")
public class ProtectMod extends Mod implements UpdateListener
{
	private EntityLivingBase friend;
	private EntityLivingBase enemy;
	private float range = 6F;
	private double distanceF = 2D;
	private double distanceE = 3D;
	private float speed;
	
	@Override
	public String getRenderName()
	{
		if(friend != null)
			return "Protecting " + friend.getName();
		else
			return "Protect";
	}
	
	@Override
	public void onEnable()
	{
		friend = null;
		EntityLivingBase en = EntityUtils.getClosestEntity(false, true, false);
		if(en != null && mc.thePlayer.getDistanceToEntity(en) <= range)
			friend = en;
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(friend == null || friend.isDead || friend.getHealth() <= 0
			|| mc.thePlayer.getHealth() <= 0)
		{
			friend = null;
			enemy = null;
			setEnabled(false);
			return;
		}
		if(enemy != null && (enemy.getHealth() <= 0 || enemy.isDead))
			enemy = null;
		double xDistF = Math.abs(mc.thePlayer.posX - friend.posX);
		double zDistF = Math.abs(mc.thePlayer.posZ - friend.posZ);
		double xDistE = distanceE;
		double zDistE = distanceE;
		if(enemy != null && mc.thePlayer.getDistanceToEntity(enemy) <= range)
		{
			xDistE = Math.abs(mc.thePlayer.posX - enemy.posX);
			zDistE = Math.abs(mc.thePlayer.posZ - enemy.posZ);
		}else
			EntityUtils.faceEntityClient(friend);
		if((xDistF > distanceF || zDistF > distanceF)
			&& (enemy == null || mc.thePlayer.getDistanceToEntity(enemy) > range)
			|| xDistE > distanceE || zDistE > distanceE)
			mc.gameSettings.keyBindForward.pressed = true;
		else
			mc.gameSettings.keyBindForward.pressed = false;
		if(mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround)
			mc.thePlayer.jump();
		if(mc.thePlayer.isInWater() && mc.thePlayer.posY < friend.posY)
			mc.thePlayer.motionY += 0.04;
		if(wurst.mods.yesCheatMod.isActive())
			speed = wurst.mods.killauraMod.yesCheatSpeed;
		else
			speed = wurst.mods.killauraMod.normalSpeed;
		updateMS();
		if(hasTimePassedS(speed) && EntityUtils.getClosestEnemy(friend) != null)
		{
			enemy = EntityUtils.getClosestEnemy(friend);
			if(mc.thePlayer.getDistanceToEntity(enemy) <= range)
			{
				if(wurst.mods.autoSwordMod.isActive())
					AutoSwordMod.setSlot();
				wurst.mods.criticalsMod.doCritical();
				wurst.mods.blockHitMod.doBlock();
				EntityUtils.faceEntityClient(enemy);
				mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
				mc.playerController.attackEntity(mc.thePlayer, enemy);
				updateLastMS();
			}
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		if(friend != null)
			mc.gameSettings.keyBindForward.pressed = false;
	}
	
	public void setFriend(EntityLivingBase friend)
	{
		this.friend = friend;
	}
}
