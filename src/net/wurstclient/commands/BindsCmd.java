/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.commands;

import java.util.Iterator;
import java.util.Map.Entry;

import net.wurstclient.commands.Cmd.Info;
import net.wurstclient.utils.MiscUtils;

import java.util.TreeSet;

@Info(description = "Lists all keybinds.",
	name = "binds",
	syntax = {"[<page>]"},
	help = "Commands/binds")
public class BindsCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length == 0)
		{
			execute(new String[]{"1"});
			return;
		}
		int pages = (int)Math.ceil(wurst.keybinds.size() / 8D);
		if(MiscUtils.isInteger(args[0]))
		{
			int page = Integer.valueOf(args[0]);
			if(page > pages || page == 0)
			{
				syntaxError("Invalid page: " + page);
				return;
			}
			wurst.chat.message("Current keybinds: "
				+ Integer.toString(wurst.keybinds.size()));
			wurst.chat.message("Keybind list (page " + page + "/" + pages
				+ "):");
			Iterator<Entry<String, TreeSet<String>>> itr =
				wurst.keybinds.entrySet().iterator();
			for(int i = 0; itr.hasNext(); i++)
			{
				Entry<String, TreeSet<String>> entry = itr.next();
				
				if(i >= (page - 1) * 8 && i < (page - 1) * 8 + 8)
					entry.getValue()
						.forEach(
							(cmd) -> wurst.chat.message(entry.getKey() + ": "
								+ cmd));
			}
		}else
			syntaxError("Not a number: \"" + args[0] + "\"");
	}
}
