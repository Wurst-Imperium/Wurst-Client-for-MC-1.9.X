/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class PathFinder
{
	private BlockPos goal;
	private PriorityQueue<PathPoint> queue;
	private HashMap<BlockPos, PathPoint> processed =
		new HashMap<BlockPos, PathPoint>();
	private PathPoint lastPoint;
	
	public PathFinder(BlockPos goal)
	{
		this(new BlockPos(Minecraft.getMinecraft().thePlayer), goal);
	}
	
	public PathFinder(BlockPos start, BlockPos goal)
	{
		this.goal = goal;
		queue = new PriorityQueue<PathPoint>(new Comparator<PathPoint>()
		{
			@Override
			public int compare(PathPoint o1, PathPoint o2)
			{
				if(o1.getPriority() < o2.getPriority())
					return -1;
				else if(o1.getPriority() > o2.getPriority())
					return 1;
				else if(getDistance(o1.getPos(), PathFinder.this.goal) < getDistance(
					o2.getPos(), PathFinder.this.goal))
					return -1;
				else if(getDistance(o1.getPos(), PathFinder.this.goal) > getDistance(
					o2.getPos(), PathFinder.this.goal))
					return 1;
				else
					return 0;
			}
		});
		addPoint(start, null, 0, 0);
	}
	
	public boolean find()
	{
		long startTime = System.currentTimeMillis();
		boolean foundPath = false;
		while(!queue.isEmpty())
		{
			lastPoint = queue.poll();
			processed.put(lastPoint.getPos(), lastPoint);
			if(lastPoint.getPos().equals(goal))
			{
				foundPath = true;
				break;
			}
			if(System.currentTimeMillis() - startTime > 10e3)
			{
				System.err
					.println("Path finding took more than 10s. Aborting!");
				break;
			}
			for(BlockPos next : lastPoint.getNeighbors())
			{
				if(!PathUtils.isSafe(next))
					continue;
				int nextCost = PathUtils.getCost(lastPoint.getPos(), next);
				int newCost = lastPoint.getMovementCost() + nextCost;
				if(!processed.containsKey(next)
					|| processed.get(next).getMovementCost() > newCost)
					addPoint(next, lastPoint, newCost,
						newCost + getDistance(next, goal) * nextCost);
			}
		}
		System.out.println("Processed " + processed.size() + " nodes");
		return foundPath;
	}
	
	private void addPoint(BlockPos pos, PathPoint previous, int movementCost,
		int priority)
	{
		queue.add(new PathPoint(pos, previous, movementCost, priority));
	}
	
	private int getDistance(BlockPos a, BlockPos b)
	{
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY())
			+ Math.abs(a.getZ() - b.getZ());
	}
	
	public PathPoint getRawPath()
	{
		return lastPoint;
	}
	
	public ArrayList<BlockPos> formatPath()
	{
		ArrayList<BlockPos> path = new ArrayList<BlockPos>();
		PathPoint point = lastPoint;
		while(point != null)
		{
			path.add(point.getPos());
			point = point.getPrevious();
		}
		Collections.reverse(path);
		for(int i = path.size() - 1; i > 1; i--)
			if(path.get(i).getX() == path.get(i - 2).getX()
				&& path.get(i).getY() == path.get(i - 2).getY()
				|| path.get(i).getX() == path.get(i - 2).getX()
				&& path.get(i).getZ() == path.get(i - 2).getZ()
				|| path.get(i).getY() == path.get(i - 2).getY()
				&& path.get(i).getZ() == path.get(i - 2).getZ())
				path.remove(i - 1);
		return path;
	}
}
