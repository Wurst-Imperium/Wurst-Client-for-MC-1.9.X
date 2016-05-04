/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.ai;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class PathPoint
{
	private BlockPos pos;
	private PathPoint previous;
	private int priority;
	private int movementCost;
	
	public PathPoint(BlockPos pos, PathPoint previous, int movementCost,
		int priority)
	{
		this.pos = pos;
		this.previous = previous;
		this.movementCost = movementCost;
		this.priority = priority;
	}
	
	public ArrayList<BlockPos> getNeighbors()
	{
		BlockPos playerPos = new BlockPos(Minecraft.getMinecraft().thePlayer);
		ArrayList<BlockPos> neighbors = new ArrayList<BlockPos>();
		neighbors.add(pos.add(0, 0, -1));// north
		neighbors.add(pos.add(0, 0, 1));// south
		neighbors.add(pos.add(1, 0, 0));// east
		neighbors.add(pos.add(-1, 0, 0));// west
		for(int i = neighbors.size() - 1; i > -1; i--)
		{
			BlockPos neighbor = neighbors.get(i);
			if(!PathUtils.isSafe(neighbor)
				|| !PathUtils.isSafe(neighbor.add(0, 1, 0))
				|| Math.abs(playerPos.getX() - neighbor.getX()) > 256
				|| Math.abs(playerPos.getZ() - neighbor.getZ()) > 256)
				neighbors.remove(i);
			else if(!PathUtils.isFlyable(neighbor))
				if(!PathUtils.isFallable(neighbor))
					neighbors.remove(i);
				else if(!PathUtils.isSolid(pos.add(0, -1, 0)))
					if(!PathUtils.isSolid(neighbor.add(0, -1, 0)))
						neighbors.remove(i);
					else if(previous == null
						|| PathUtils.isSolid(previous.getPos().add(0, -1, 0))
						&& previous.getPos().getY() >= pos.getY())
						neighbors.remove(i);
		}
		neighbors.add(pos.add(0, -1, 0));// down
		if(PathUtils.isFlyable(pos) || PathUtils.isClimbable(pos))
			neighbors.add(pos.add(0, 1, 0));// up
		return neighbors;
	}
	
	public BlockPos getPos()
	{
		return pos;
	}
	
	public PathPoint getPrevious()
	{
		return previous;
	}
	
	public int getPriority()
	{
		return priority;
	}
	
	public int getMovementCost()
	{
		return movementCost;
	}
}
