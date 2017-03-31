/*
 * Copyright © 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.commands;

import java.util.Iterator;

import net.wurstclient.utils.ChatUtils;
import net.wurstclient.utils.MiscUtils;

@Cmd.Info(description = "Shows the command list or the help for a command.",
	name = "help",
	syntax = {"[<page>]", "[<command>]"},
	help = "Commands/help")
public class HelpCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws CmdError
	{
		if(args.length == 0)
		{
			execute(new String[]{"1"});
			return;
		}
		int pages = (int)Math.ceil(wurst.commands.countCommands() / 8D);
		if(MiscUtils.isInteger(args[0]))
		{
			int page = Integer.valueOf(args[0]);
			if(page > pages || page < 1)
				syntaxError("Invalid page: " + page);
			ChatUtils.message(
				"Available commands: " + wurst.commands.countCommands());
			ChatUtils
				.message("Command list (page " + page + "/" + pages + "):");
			Iterator<Cmd> itr = wurst.commands.getAllCommands().iterator();
			for(int i = 0; itr.hasNext(); i++)
			{
				Cmd cmd = itr.next();
				if(i >= (page - 1) * 8 && i < (page - 1) * 8 + 8)
					ChatUtils.message(cmd.getCmdName());
			}
		}else
		{
			Cmd cmd = wurst.commands.getCommandByName(args[0]);
			if(cmd != null)
			{
				ChatUtils.message("Available help for ." + args[0] + ":");
				cmd.printHelp();
				cmd.printSyntax();
			}else
				error("Command \"" + args[0] + "\" could not be found.");
		}
	}
}
