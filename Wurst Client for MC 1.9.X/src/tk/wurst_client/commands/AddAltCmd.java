/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import java.util.Iterator;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;
import tk.wurst_client.alts.Alt;
import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.gui.alts.GuiAltList;

@Info(help = "Adds a player or all players on a server to your alt list.",
	name = "addalt",
	syntax = {"<player>", "all"})
public class AddAltCmd extends Cmd
{
	@Override
	public void execute(String[] args) throws Error
	{
		if(args.length != 1)
			syntaxError();
		if(args[0].equals("all"))
		{
			int alts = 0;
			Iterator itr = mc.getNetHandler().getPlayerInfoMap().iterator();
			while(itr.hasNext())
			{
				NetworkPlayerInfo info = (NetworkPlayerInfo)itr.next();
				String crackedName =
					StringUtils.stripControlCodes(info.getPlayerNameForReal());
				if(crackedName.equals(mc.thePlayer.getName())
					|| crackedName.equals("Alexander01998")
					|| GuiAltList.alts.contains(new Alt(crackedName)))
					continue;
				GuiAltList.alts.add(new Alt(crackedName));
				alts++;
			}
			if(alts == 1)
				wurst.chat.message("Added 1 alt to the alt list.");
			else
				wurst.chat.message("Added " + alts + " alts to the alt list.");
			GuiAltList.sortAlts();
			wurst.files.saveAlts();
		}else if(!args[0].equals("Alexander01998"))
		{
			GuiAltList.alts.add(new Alt(args[0]));
			GuiAltList.sortAlts();
			wurst.files.saveAlts();
			wurst.chat.message("Added \"" + args[0] + "\" to the alt list.");
		}
	}
}
