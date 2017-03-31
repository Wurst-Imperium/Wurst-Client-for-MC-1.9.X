/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import net.wurstclient.utils.MiscUtils;

@Cmd.Info(
	description = "Teleports you up/down. Can glitch you through floors & "
		+ "ceilings.\nThe maximum distance is 100 blocks on vanilla servers and "
		+ "10 blocks on Bukkit servers.",
	name = "vclip",
	syntax = {"<height>"},
	help = "Commands/vclip")
public class VClipCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length != 1)
			syntaxError();
		if(MiscUtils.isInteger(args[0]))
			mc.thePlayer.setPosition(mc.thePlayer.posX,
				mc.thePlayer.posY + Integer.valueOf(args[0]),
				mc.thePlayer.posZ);
		else
			syntaxError();
	}
}
