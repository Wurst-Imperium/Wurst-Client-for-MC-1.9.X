/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import java.util.Iterator;

import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.utils.MiscUtils;

@Info(help = "Manages your friends list.", name = "friends", syntax = {
	"(add | remove) <player>", "list [<page>]"})
public class FriendsCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length == 0)
			syntaxError();
		if(args[0].equalsIgnoreCase("list"))
		{
			if(args.length == 1)
			{
				execute(new String[]{"list", "1"});
				return;
			}
			int pages = (int)Math.ceil(wurst.friends.size() / 8D);
			if(MiscUtils.isInteger(args[1]))
			{
				int page = Integer.valueOf(args[1]);
				if(page > pages || page < 1)
					syntaxError();
				wurst.chat.message("Current friends: " + wurst.friends.size());
				wurst.chat.message("Friends list (page " + page + "/" + pages
					+ "):");
				Iterator<String> itr = wurst.friends.iterator();
				for(int i = 0; itr.hasNext(); i++)
				{
					String friend = itr.next();
					if(i >= (page - 1) * 8 && i < (page - 1) * 8 + 8)
						wurst.chat.message(friend);
				}
			}else
				syntaxError();
		}else if(args.length < 2)
			syntaxError();
		else if(args[0].equalsIgnoreCase("add"))
		{
			if(wurst.friends.contains(args[1]))
			{
				wurst.chat.error("\"" + args[1]
					+ "\" is already in your friends list.");
				return;
			}
			wurst.friends.add(args[1]);
			wurst.files.saveFriends();
			wurst.chat.message("Added friend \"" + args[1] + "\".");
		}else if(args[0].equalsIgnoreCase("remove"))
		{
			if(wurst.friends.remove(args[1]))
			{
				wurst.files.saveFriends();
				wurst.chat.message("Removed friend \"" + args[1] + "\".");
			}else
				wurst.chat.error("\"" + args[1]
					+ "\" is not in your friends list.");
		}else
			syntaxError();
	}
}
