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
import tk.wurst_client.mods.NukerMod;
import tk.wurst_client.utils.MiscUtils;

@Info(help = "Changes the settings of Nuker.", name = "nuker", syntax = {
	"mode (normal|id|flat|smash)", "id <block_id>", "name <block_name>"})
public class NukerCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		NukerMod nuker = wurst.mods.nukerMod;
		if(args.length != 2)
			syntaxError();
		else if(args[0].toLowerCase().equals("mode"))
		{
			// search mode by name
			String[] modeNames = nuker.getModes();
			String newModeName = args[1];
			int newMode = -1;
			for(int i = 0; i < modeNames.length; i++)
				if(newModeName.equals(modeNames[i].toLowerCase()))
					newMode = i;
			
			// syntax error if mode does not exist
			if(newMode == -1)
				syntaxError("Invalid mode");
			
			if(newMode != nuker.getMode())
			{
				nuker.setMode(newMode);
				wurst.files.saveNavigatorData();
			}
			
			wurst.chat.message("Nuker mode set to \"" + args[1] + "\".");
		}else if(args[0].equalsIgnoreCase("id") && MiscUtils.isInteger(args[1]))
		{
			if(nuker.getMode() != 1)
			{
				nuker.setMode(1);
				wurst.files.saveNavigatorData();
				wurst.chat.message("Nuker mode set to \"" + args[0] + "\".");
			}
			
			NukerMod.id = Integer.valueOf(args[1]);
			wurst.chat.message("Nuker ID set to \"" + args[1] + "\".");
		}else if(args[0].equalsIgnoreCase("name"))
		{
			if(nuker.getMode() != 1)
			{
				nuker.setMode(1);
				wurst.files.saveNavigatorData();
				wurst.chat.message("Nuker mode set to \"" + args[0] + "\".");
			}
			
			int newId = Block.getIdFromBlock(Block.getBlockFromName(args[1]));
			if(newId == -1)
				error("The block \"" + args[1] + "\" could not be found.");
			
			NukerMod.id = newId;
			wurst.chat.message("Nuker ID set to " + newId + " (" + args[1]
				+ ").");
		}else
			syntaxError();
	}
}
