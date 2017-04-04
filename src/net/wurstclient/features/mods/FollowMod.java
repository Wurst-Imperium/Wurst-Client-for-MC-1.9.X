/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.entity.Entity;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.EntityUtils;
import net.wurstclient.utils.EntityUtils.TargetSettings;
import net.wurstclient.utils.RotationUtils;

@Mod.Info(
	description = "A bot that follows the closest entity.\n" + "Very annoying.",
	name = "Follow",
	help = "Mods/Follow")
@Mod.Bypasses(ghostMode = false)
@Mod.DontSaveState
public final class FollowMod extends Mod implements UpdateListener
{
	private Entity entity;
	private float range = 12F;
	
	private TargetSettings targetSettingsFind = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public float getRange()
		{
			return range;
		}
	};
	
	@Override
	public String getRenderName()
	{
		if(entity != null)
			return "Following " + entity.getName();
		else
			return "Follow";
	}
	
	@Override
	public void onEnable()
	{
		if(entity == null)
			entity = EntityUtils.getClosestEntity(targetSettingsFind);
		
		if(entity == null)
		{
			ChatUtils.error("Could not find a valid entity within 12 blocks.");
			setEnabled(false);
			return;
		}
		
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(entity == null)
		{
			setEnabled(false);
			return;
		}
		if(entity.isDead || WMinecraft.getPlayer().isDead)
		{
			entity = null;
			setEnabled(false);
			return;
		}
		double xDist = Math.abs(WMinecraft.getPlayer().posX - entity.posX);
		double zDist = Math.abs(WMinecraft.getPlayer().posZ - entity.posZ);
		RotationUtils.faceEntityClient(entity);
		if(xDist > 1D || zDist > 1D)
			mc.gameSettings.keyBindForward.pressed = true;
		else
			mc.gameSettings.keyBindForward.pressed = false;
		if(WMinecraft.getPlayer().isCollidedHorizontally
			&& WMinecraft.getPlayer().onGround)
			WMinecraft.getPlayer().jump();
		if(WMinecraft.getPlayer().isInWater()
			&& WMinecraft.getPlayer().posY < entity.posY)
			WMinecraft.getPlayer().motionY += 0.04;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		if(entity != null)
			mc.gameSettings.keyBindForward.pressed = false;
	}
	
	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}
}
