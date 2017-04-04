/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.utils;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.wurstclient.WurstClient;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.features.special_features.TargetSpf;

public class EntityUtils
{
	public static boolean isCorrectEntity(Object o, boolean ignoreFriends)
	{
		// non-entities
		if(!(o instanceof Entity))
			return false;
		
		// friends
		if(ignoreFriends && o instanceof EntityPlayer)
			if(WurstClient.INSTANCE.friends
				.contains(((EntityPlayer)o).getName()))
				return false;
			
		TargetSpf targetSpf = WurstClient.INSTANCE.special.targetSpf;
		
		// invisible entities
		if(((Entity)o).isInvisibleToPlayer(WMinecraft.getPlayer()))
			return targetSpf.invisibleMobs.isChecked()
				&& o instanceof EntityLiving
				|| targetSpf.invisiblePlayers.isChecked()
					&& o instanceof EntityPlayer;
		
		// players
		if(o instanceof EntityPlayer)
			return (((EntityPlayer)o).isPlayerSleeping()
				&& targetSpf.sleepingPlayers.isChecked()
				|| !((EntityPlayer)o).isPlayerSleeping()
					&& targetSpf.players.isChecked())
				&& (!targetSpf.teams.isChecked() || checkName(
					((EntityPlayer)o).getDisplayName().getFormattedText()));
		
		// animals
		if(o instanceof EntityAgeable || o instanceof EntityAmbientCreature
			|| o instanceof EntityWaterMob)
			return targetSpf.animals.isChecked()
				&& (!targetSpf.teams.isChecked() || !((Entity)o).hasCustomName()
					|| checkName(((Entity)o).getCustomNameTag()));
		
		// monsters
		if(o instanceof EntityMob || o instanceof EntitySlime
			|| o instanceof EntityFlying)
			return targetSpf.monsters.isChecked()
				&& (!targetSpf.teams.isChecked() || !((Entity)o).hasCustomName()
					|| checkName(((Entity)o).getCustomNameTag()));
		
		// golems
		if(o instanceof EntityGolem)
			return targetSpf.golems.isChecked()
				&& (!targetSpf.teams.isChecked() || !((Entity)o).hasCustomName()
					|| checkName(((Entity)o).getCustomNameTag()));
		
		return false;
	}
	
	private static boolean checkName(String name)
	{
		// check colors
		String[] colors = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"a", "b", "c", "d", "e", "f"};
		boolean[] teamColors =
			WurstClient.INSTANCE.special.targetSpf.teamColors.getSelected();
		boolean hasKnownColor = false;
		for(int i = 0; i < 16; i++)
			if(name.contains("§" + colors[i]))
			{
				hasKnownColor = true;
				if(teamColors[i])
					return true;
			}
		
		// no known color => white
		return !hasKnownColor && teamColors[15];
	}
	
	public static EntityLivingBase getClosestEntity(boolean ignoreFriends,
		float fov, boolean hitThroughWalls)
	{
		EntityLivingBase closestEntity = null;
		for(Object o : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(o, ignoreFriends)
				&& RotationUtils.getAngleToClientRotation(
					((Entity)o).boundingBox.getCenter()) <= fov / 2)
			{
				EntityLivingBase en = (EntityLivingBase)o;
				if(!(o instanceof EntityPlayerSP) && !en.isDead
					&& en.getHealth() > 0
					&& (hitThroughWalls
						|| WMinecraft.getPlayer().canEntityBeSeen(en))
					&& !en.getName().equals(WMinecraft.getPlayer().getName()))
					if(closestEntity == null || WMinecraft.getPlayer()
						.getDistanceToEntity(en) < WMinecraft.getPlayer()
							.getDistanceToEntity(closestEntity))
						closestEntity = en;
			}
		return closestEntity;
	}
	
	public static ArrayList<EntityLivingBase> getCloseEntities(
		boolean ignoreFriends, float range, boolean hitThroughWalls)
	{
		ArrayList<EntityLivingBase> closeEntities = new ArrayList<>();
		for(Object o : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(o, ignoreFriends))
			{
				EntityLivingBase en = (EntityLivingBase)o;
				if(!(o instanceof EntityPlayerSP) && !en.isDead
					&& en.getHealth() > 0
					&& (hitThroughWalls
						|| WMinecraft.getPlayer().canEntityBeSeen(en))
					&& !en.getName().equals(WMinecraft.getPlayer().getName())
					&& WMinecraft.getPlayer().getDistanceToEntity(en) <= range)
					closeEntities.add(en);
			}
		return closeEntities;
	}
	
	public static EntityLivingBase getClosestEntityRaw(boolean ignoreFriends)
	{
		EntityLivingBase closestEntity = null;
		for(Object o : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(o, ignoreFriends))
			{
				EntityLivingBase en = (EntityLivingBase)o;
				if(!(o instanceof EntityPlayerSP) && !en.isDead
					&& en.getHealth() > 0)
					if(closestEntity == null || WMinecraft.getPlayer()
						.getDistanceToEntity(en) < WMinecraft.getPlayer()
							.getDistanceToEntity(closestEntity))
						closestEntity = en;
			}
		return closestEntity;
	}
	
	public static EntityLivingBase getClosestEnemy(EntityLivingBase friend)
	{
		EntityLivingBase closestEnemy = null;
		for(Object o : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(o, true))
			{
				EntityLivingBase en = (EntityLivingBase)o;
				if(!(o instanceof EntityPlayerSP) && o != friend && !en.isDead
					&& en.getHealth() <= 0 == false
					&& WMinecraft.getPlayer().canEntityBeSeen(en))
					if(closestEnemy == null || WMinecraft.getPlayer()
						.getDistanceToEntity(en) < WMinecraft.getPlayer()
							.getDistanceToEntity(closestEnemy))
						closestEnemy = en;
			}
		return closestEnemy;
	}
	
	public static EntityLivingBase searchEntityByIdRaw(UUID ID)
	{
		EntityLivingBase newEntity = null;
		for(Object o : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(o, false))
			{
				EntityLivingBase en = (EntityLivingBase)o;
				if(!(o instanceof EntityPlayerSP) && !en.isDead)
					if(newEntity == null && en.getUniqueID().equals(ID))
						newEntity = en;
			}
		return newEntity;
	}
	
	public static EntityLivingBase searchEntityByName(String name)
	{
		EntityLivingBase newEntity = null;
		for(Object o : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(o, false))
			{
				EntityLivingBase en = (EntityLivingBase)o;
				if(!(o instanceof EntityPlayerSP) && !en.isDead
					&& WMinecraft.getPlayer().canEntityBeSeen(en))
					if(newEntity == null && en.getName().equals(name))
						newEntity = en;
			}
		return newEntity;
	}
	
	public static EntityLivingBase searchEntityByNameRaw(String name)
	{
		EntityLivingBase newEntity = null;
		for(Object o : WMinecraft.getWorld().loadedEntityList)
			if(isCorrectEntity(o, false))
			{
				EntityLivingBase en = (EntityLivingBase)o;
				if(!(o instanceof EntityPlayerSP) && !en.isDead)
					if(newEntity == null && en.getName().equals(name))
						newEntity = en;
			}
		return newEntity;
	}
}
