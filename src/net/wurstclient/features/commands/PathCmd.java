/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.ai.PathFinder;
import net.wurstclient.ai.PathPoint;
import net.wurstclient.events.listeners.RenderListener;
import net.wurstclient.features.commands.Cmd.Info;
import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.RenderUtils;

@Info(
	description = "Shows the shortest path to a specific point. Useful for labyrinths and caves.",
	name = "path",
	syntax = {"<x> <y> <z>", "<entity>"},
	help = "Commands/path")
public class PathCmd extends Cmd implements RenderListener
{
	private PathPoint path;
	private boolean enabled;
	
	@Override
	public void execute(String[] args) throws CmdError
	{
		path = null;
		if(enabled)
		{
			wurst.events.remove(RenderListener.class, this);
			enabled = false;
			return;
		}
		int[] posArray = argsToPos(args);
		final BlockPos pos =
			new BlockPos(posArray[0], posArray[1], posArray[2]);
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.println("Finding path");
				long startTime = System.nanoTime();
				PathFinder pathFinder = new PathFinder(pos);
				if(pathFinder.find())
				{
					path = pathFinder.getRawPath();
					enabled = true;
					wurst.events.add(RenderListener.class, PathCmd.this);
				}else
					ChatUtils.error("Could not find a path.");
				System.out.println("Done after "
					+ (System.nanoTime() - startTime) / 1e6 + "ms");
			}
		});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	@Override
	public void onRender(float partialTicks)
	{
		PathPoint path2 = path;
		while(path2 != null)
		{
			RenderUtils.blockESPBox(path2.getPos());
			path2 = path2.getPrevious();
		}
	}
}
