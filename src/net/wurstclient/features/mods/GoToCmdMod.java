/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathUtils;
import net.wurstclient.compatibility.WMinecraft;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.utils.BlockUtils;

@Mod.Info(description = "", name = "GoTo")
@Mod.Bypasses
public class GoToCmdMod extends Mod implements UpdateListener
{
	private static ArrayList<BlockPos> path;
	private static BlockPos goal;
	private int index;
	
	@Override
	public String getRenderName()
	{
		if(goal != null)
			return "Go to " + goal.getX() + " " + goal.getY() + " "
				+ goal.getZ();
		else
			return "GoTo";
	}
	
	@Override
	public void onEnable()
	{
		index = 0;
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(path == null || goal == null)
		{
			setEnabled(false);
			return;
		}
		BlockPos currentPos = new BlockPos(WMinecraft.getPlayer());
		BlockPos nextPos = path.get(index);
		float dist = BlockUtils.getPlayerBlockDistance(nextPos);
		float hDist = BlockUtils.getHorizontalPlayerBlockDistance(nextPos);
		double vDist = Math.abs(WMinecraft.getPlayer().posY - nextPos.getY());
		mc.gameSettings.keyBindForward.pressed = false;
		mc.gameSettings.keyBindBack.pressed = false;
		mc.gameSettings.keyBindRight.pressed = false;
		mc.gameSettings.keyBindLeft.pressed = false;
		mc.gameSettings.keyBindJump.pressed = false;
		mc.gameSettings.keyBindSneak.pressed = false;
		WMinecraft.getPlayer().rotationPitch = 10;
		BlockUtils.faceBlockClientHorizontally(nextPos);
		
		if(hDist > 0.25)
			mc.gameSettings.keyBindForward.pressed = true;
		if(vDist > 0.75)
			if(PathUtils.isFlyable(currentPos))
			{
				if(currentPos.getY() > nextPos.getY())
					mc.gameSettings.keyBindSneak.pressed = true;
				else
					mc.gameSettings.keyBindJump.pressed = true;
			}else if(PathUtils.isClimbable(currentPos)
				&& currentPos.getY() < nextPos.getY())
			{
				BlockPos[] neighbors = new BlockPos[]{currentPos.add(0, 0, -1),
					currentPos.add(0, 0, 1), currentPos.add(1, 0, 0),
					currentPos.add(-1, 0, 0)};
				for(BlockPos neigbor : neighbors)
				{
					if(!PathUtils.isSolid(neigbor))
						continue;
					BlockUtils.faceBlockClientHorizontally(neigbor);
					mc.gameSettings.keyBindForward.pressed = true;
					break;
				}
			}
		
		if(dist < 1)
			index++;
		if(index >= path.size())
			setEnabled(false);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		path = null;
		goal = null;
		mc.gameSettings.keyBindForward.pressed = false;
		mc.gameSettings.keyBindBack.pressed = false;
		mc.gameSettings.keyBindRight.pressed = false;
		mc.gameSettings.keyBindLeft.pressed = false;
		mc.gameSettings.keyBindJump.pressed = false;
		mc.gameSettings.keyBindSneak.pressed = false;
	}
	
	public static void setPath(ArrayList<BlockPos> path)
	{
		GoToCmdMod.path = path;
	}
	
	public static BlockPos getGoal()
	{
		return goal;
	}
	
	public static void setGoal(BlockPos goal)
	{
		GoToCmdMod.goal = goal;
	}
}
