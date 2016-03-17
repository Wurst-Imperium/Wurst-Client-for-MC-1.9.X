/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import net.minecraft.block.Block;
import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.utils.MiscUtils;

@Info(help = "Changes the settings of Search or toggles it.",
	name = "search",
	syntax = {"id <block_id>", "name <block_name>"})
public class SearchCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length == 0)
		{
			wurst.mods.searchMod.toggle();
			wurst.chat.message("Search turned "
				+ (wurst.mods.searchMod.isEnabled() == true ? "on" : "off")
				+ ".");
		}else if(args.length == 2)
		{
			if(args[0].toLowerCase().equals("id"))
			{
				if(MiscUtils.isInteger(args[1]))
					wurst.options.searchID = Integer.valueOf(args[1]);
				else
					syntaxError("ID must be a number.");
				wurst.files.saveOptions();
				wurst.mods.searchMod.notify = true;
				wurst.chat.message("Search ID set to " + args[1] + ".");
			}else if(args[0].equalsIgnoreCase("name"))
			{
				int newID =
					Block.getIdFromBlock(Block.getBlockFromName(args[1]));
				if(newID == -1)
					error("Block \"" + args[1] + "\" could not be found.");
				wurst.options.searchID = Integer.valueOf(newID);
				wurst.files.saveOptions();
				wurst.mods.searchMod.notify = true;
				wurst.chat.message("Search ID set to " + newID + " (" + args[1]
					+ ").");
			}
		}else
			syntaxError();
	}
}
