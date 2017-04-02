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
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Category;
import net.wurstclient.features.mods.Mod.Info;
import net.wurstclient.utils.EntityUtils;

@Info(category = Category.COMBAT,
	description = "A bot that follows the closest entity and protects it.",
	name = "Protect",
	help = "Mods/Protect")
@Bypasses(ghostMode = false)
public class ProtectMod extends Mod implements UpdateListener
{
	private EntityLivingBase friend;
	private EntityLivingBase enemy;
	private float range = 6F;
	private double distanceF = 2D;
	private double distanceE = 3D;
	
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
		EntityLivingBase en = EntityUtils.getClosestEntity(false, 360, false);
		if(en != null
			&& WMinecraft.getPlayer().getDistanceToEntity(en) <= range)
			friend = en;
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(friend == null || friend.isDead || friend.getHealth() <= 0
			|| WMinecraft.getPlayer().getHealth() <= 0)
		{
			friend = null;
			enemy = null;
			setEnabled(false);
			return;
		}
		if(enemy != null && (enemy.getHealth() <= 0 || enemy.isDead))
			enemy = null;
		double xDistF = Math.abs(WMinecraft.getPlayer().posX - friend.posX);
		double zDistF = Math.abs(WMinecraft.getPlayer().posZ - friend.posZ);
		double xDistE = distanceE;
		double zDistE = distanceE;
		if(enemy != null
			&& WMinecraft.getPlayer().getDistanceToEntity(enemy) <= range)
		{
			xDistE = Math.abs(WMinecraft.getPlayer().posX - enemy.posX);
			zDistE = Math.abs(WMinecraft.getPlayer().posZ - enemy.posZ);
		}else
			EntityUtils.faceEntityClient(friend);
		if((xDistF > distanceF || zDistF > distanceF)
			&& (enemy == null
				|| WMinecraft.getPlayer().getDistanceToEntity(enemy) > range)
			|| xDistE > distanceE || zDistE > distanceE)
			mc.gameSettings.keyBindForward.pressed = true;
		else
			mc.gameSettings.keyBindForward.pressed = false;
		if(WMinecraft.getPlayer().isCollidedHorizontally
			&& WMinecraft.getPlayer().onGround)
			WMinecraft.getPlayer().jump();
		if(WMinecraft.getPlayer().isInWater()
			&& WMinecraft.getPlayer().posY < friend.posY)
			WMinecraft.getPlayer().motionY += 0.04;
		updateMS();
		if(hasTimePassedS(wurst.mods.killauraMod.speed.getValueF())
			&& EntityUtils.getClosestEnemy(friend) != null)
		{
			enemy = EntityUtils.getClosestEnemy(friend);
			if(WMinecraft.getPlayer().getDistanceToEntity(enemy) <= range)
			{
				if(wurst.mods.autoSwordMod.isActive())
					AutoSwordMod.setSlot();
				wurst.mods.criticalsMod.doCritical();
				wurst.mods.blockHitMod.doBlock();
				EntityUtils.faceEntityClient(enemy);
				WMinecraft.getPlayer().swingArm(EnumHand.MAIN_HAND);
				mc.playerController.attackEntity(WMinecraft.getPlayer(), enemy);
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
